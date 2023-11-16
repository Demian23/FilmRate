package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

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
             return Optional.of(new User(rs.getInt("user_id"), rs.getInt("user_rate")));
        } else {
            return Optional.empty();
        }
    }
}
