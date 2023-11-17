package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SQLUserDAO implements UserDAO {

    static private ConnectionPool pool = null;
    Access accessToDataBase;
    public SQLUserDAO(Access access, ConnectionPool poolInstance){
        accessToDataBase = access;
        if(pool == null)
            pool = poolInstance;
    }
    @Override
    public void registration(NewUser user) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            regNewUser(user, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }

    }

    private void regNewUser(NewUser user, AutoClosableList autoClosable) throws ConnectionPoolException, DAOException, SQLException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add(con);
        if(!isUserExist(user.getUserName(), con, autoClosable)){
            tryInsertNewMain(user.getMail(), con, autoClosable);
            int mailId = queryMailId(user.getMail(), con, autoClosable);
            insertNewUser(user, mailId, con, autoClosable);
        }else{
            throw new DAOException("User with name: " + user.getUserName() + " already exist!");
        }

    }
    private void insertNewUser(NewUser user, int mailId, Connection con, AutoClosableList autoClosable) throws SQLException, DAOException {
        String sql = "INSERT INTO `user` (`name`, `password_hash`, `mail_id`, `user_rate`)" +
                "VALUES(?, ?, ?, ?)";
        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add( preSt);

        preSt.setString(1, user.getUserName());
        preSt.setString(2, user.getNewPassword());
        preSt.setInt(3, mailId);
        preSt.setInt(4, 0);

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected == 0){
            throw new DAOException("Can't add user: " + user.getUserName());
        }

    }
    boolean isUserExist(String name, Connection con, AutoClosableList autoClosable) throws SQLException {
        String sql = "SELECT `user_id` FROM `user` WHERE `name` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add( preSt);

        preSt.setString(1, name);

        ResultSet rs = preSt.executeQuery();
        autoClosable.add(rs);

        return rs.next();
    }

    private void tryInsertNewMain(String mail, Connection con, AutoClosableList autoClosable) throws SQLException, DAOException {
        String sql = "INSERT INTO `mail` (`mail_value`, `is_verified`, `verification_time`)" +
                "VALUES(?, ?, ?)";

        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add(preSt);

        preSt.setString(1, mail);
        preSt.setInt(2, 0);
        preSt.setInt(3, 0);

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected == 0){
            throw new DAOException("Such mail already exist");
        }
    }
    private int queryMailId(String mail, Connection con , AutoClosableList closableList) throws SQLException, DAOException {
        String sql = "SELECT `mail_id` FROM `mail` WHERE `mail_value` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closableList.add( preSt);

        preSt.setString(1, mail);

        ResultSet rs = preSt.executeQuery();
        closableList.add(rs);

        if(rs.next()){
            return rs.getInt("mail_id");
        }else{
            throw new DAOException("No such mail: " + mail);
        }
    }


    @Override
    public Optional<User> getUser(UserCredentials credentials) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryUser(credentials, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }


    private Optional<User> queryUser(UserCredentials credentials, AutoClosableList closableList) throws ConnectionPoolException, SQLException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closableList.add(con);

        String sql = "SELECT `user_id`, `user_rate` FROM `user` WHERE `name` = ? AND `password_hash` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closableList.add( preSt);

        preSt.setString(1, credentials.getName());
        preSt.setString(2, credentials.getPasswordHash());

        ResultSet rs = preSt.executeQuery();
        closableList.add( rs);

        if (rs.next()) {
             return Optional.of(new User(rs.getInt("user_id"), rs.getInt("user_rate"),
                     rs.getString("name")));
        } else {
            return Optional.empty();
        }
    }
    @Override
    public List<User> getAllUsers() throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryAllUsers(autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }


    private List<User> queryAllUsers(AutoClosableList autoClosable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add(con);

        Statement st = con.createStatement();
        autoClosable.add(st);

        String sql = "SELECT * FROM `user`";
        ResultSet rs = st.executeQuery(sql);
        autoClosable.add(rs);

        ArrayList<User> resultList = new ArrayList<>();

        while (rs.next()) {
            resultList.add(new User(rs.getInt("user_id"),
                rs.getInt("user_rate"), rs.getString("name")));
        }
        return resultList;
    }

    @Override
    public Map<Integer, UserInfoAggregation> getAllUsersAggregationInfo() throws DAOException {
        List<User> users = getAllUsers();
        Map<Integer, UserInfoAggregation> fullUsersInfo = fillFullUserInfo(users);
        fillAllBannedUsers(fullUsersInfo);
        return fullUsersInfo;
    }

    private void fillAllBannedUsers(Map<Integer, UserInfoAggregation> fullUsersInfo) throws DAOException {
        List<BannedUser> allBannedUsers = getAllBannedUsers();
        for(BannedUser bannedUser : allBannedUsers){
            UserInfoAggregation fullInfo =  fullUsersInfo.get(bannedUser.getUserId());
            if(fullInfo != null){
                fullInfo.setBanned(true);
                fullInfo.setStartBanPeriod(bannedUser.getStartPeriod());
                fullInfo.setEndBanPeriod(bannedUser.getEndPeriod());
                fullInfo.setAdminBannedId(bannedUser.getAdminBannedId());
                fullInfo.setAdminBannedName(SQLCommonDAO.getAdminName());
            }
        }
    }


    private List<BannedUser> getAllBannedUsers() throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryAllBannedUsers(autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private List<BannedUser> queryAllBannedUsers(AutoClosableList autoClosable) throws ConnectionPoolException, SQLException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add(con);

        Statement st = con.createStatement();
        autoClosable.add(st);

        String sql = "SELECT * FROM `banned_user`";
        ResultSet rs = st.executeQuery(sql);
        autoClosable.add(rs);

        ArrayList<BannedUser> resultList = new ArrayList<>();

        while (rs.next()) {
            resultList.add(createBannedUser(rs));
        }
        return resultList;
    }

    private BannedUser createBannedUser(ResultSet rs) throws SQLException {
        BannedUser bannedUser = new BannedUser();
        bannedUser.setAdminBannedId(rs.getInt("admin_banned"));
        bannedUser.setUserId(rs.getInt("banned_user_id"));
        Timestamp start = rs.getTimestamp("start");
        bannedUser.setStartPeriod(start.toString());
        Timestamp end = rs.getTimestamp("end");
        bannedUser.setEndPeriod(end.toString());
        return bannedUser;
    }


    private Map<Integer, UserInfoAggregation> fillFullUserInfo(List<User> users) {
        Map<Integer, UserInfoAggregation> result = new HashMap<>();
        for(User user : users){
            UserInfoAggregation fullInfo = new UserInfoAggregation();
            fullInfo.setUser(user);
            result.put(fullInfo.getUser().getUserId(), fullInfo);
        }
        return result;
    }


}
