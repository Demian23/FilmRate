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
