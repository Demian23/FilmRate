package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
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
    public Optional<UserWithBan> getUser(UserCredentials credentials) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryUser(credentials, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }


    private Optional<UserWithBan> queryUser(UserCredentials credentials, AutoClosableList closableList) throws ConnectionPoolException, SQLException, DAOException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closableList.add(con);

        String sql = "SELECT * FROM `user` WHERE `name` = ? AND `password_hash` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closableList.add( preSt);

        preSt.setString(1, credentials.getName());
        preSt.setString(2, credentials.getPasswordHash());

        ResultSet rs = preSt.executeQuery();
        closableList.add(rs);

        if (rs.next()) {
            User newUser = new User(rs.getInt("user_id"), rs.getInt("user_rate"),
                rs.getString("name"));
            UserWithBan user = new UserWithBan();
            user.setUser(newUser);
            Optional<BannedUser> bannedUser = retrieveBannedUserById(newUser.getUserId(), con, closableList);
            user.setBanned(bannedUser.isPresent());
            bannedUser.ifPresent(user::setBannedInfo);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    private Optional<BannedUser> retrieveBannedUserById(int id, Connection con, AutoClosableList list) throws SQLException, DAOException {
        String sql = "SELECT * FROM `banned_user` WHERE `banned_user_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        list.add( preSt);

        preSt.setInt(1, id);

        ResultSet rs = preSt.executeQuery();
        list.add(rs);
        if(rs.next()){
            return Optional.of(fillBannedUser(rs, con, list));
        }else{
            return Optional.empty();
        }
    }

    private BannedUser fillBannedUser(ResultSet rs, Connection con, AutoClosableList list) throws SQLException, DAOException {
        BannedUser bannedUser = new BannedUser();
        bannedUser.setAdminBannedId(rs.getInt("admin_banned"));
        bannedUser.setUserId(rs.getInt("banned_user_id"));
        bannedUser.setStartPeriod(rs.getString("start"));
        bannedUser.setEndPeriod(rs.getString("end"));
        bannedUser.setAdminBannedName(CommonSqlRequests.getAdminName(bannedUser.getAdminBannedId(), con, list));
        return bannedUser;
    }

    @Override
    public List<UserWithBan> getAllUsers()throws DAOException{
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryAllUsers(autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private List<UserWithBan> queryAllUsers(AutoClosableList list) throws ConnectionPoolException, SQLException, DAOException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        list.add(con);
        List<UserWithBan> result = new ArrayList<>();
        List<User> users = queryUsers(con, list);
        Map<Integer, BannedUser> bannedUsers = queryBannedUsers(con, list);
        for(User user : users){
            BannedUser bannedUser = bannedUsers.get(user.getUserId());
            UserWithBan userWithBan = new UserWithBan();
            userWithBan.setUser(user);
            if(bannedUser != null){
                userWithBan.setBanned(true);
                userWithBan.setBannedInfo(bannedUser);
            }else{
                userWithBan.setBanned(false);
            }
            result.add(userWithBan);
        }
        return result;
    }

    private List<User> queryUsers(Connection con, AutoClosableList list) throws SQLException {
        Statement st = con.createStatement();
        list.add(st);

        String sql = "SELECT * FROM `user`";
        ResultSet rs = st.executeQuery(sql);
        list.add(rs);

        List<User> result = new ArrayList<>();
        while(rs.next()){
            result.add(new User(rs.getInt("user_id"),
                    rs.getInt("user_rate"), rs.getString("name")));
        }
        return result;
    }

    private Map<Integer, BannedUser> queryBannedUsers(Connection con, AutoClosableList list) throws SQLException, DAOException {
        Statement st = con.createStatement();
        list.add(st);

        String sql = "SELECT * FROM `banned_user`";
        ResultSet rs = st.executeQuery(sql);
        list.add(rs);

        Map<Integer, BannedUser> result = new HashMap<>();

        while (rs.next()) {
            BannedUser bannedUser = fillBannedUser(rs, con, list);
            result.put(bannedUser.getUserId(), bannedUser);
        }
        return result;
    }
}
