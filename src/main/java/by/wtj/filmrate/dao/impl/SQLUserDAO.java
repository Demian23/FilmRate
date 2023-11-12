package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.connectionPool.ConnectionPool;
import by.wtj.filmrate.dao.connectionPool.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class SQLUserDAO implements UserDAO {

    static private final ConnectionPool pool = ConnectionPool.getInstance();
    Access accessToDataBase;
    public SQLUserDAO(Access access){
        accessToDataBase = access;
    }
    @Override
    public void registration(NewUser user) throws DAOException {

    }

    @Override
    public int getUserId(UserCredentials user) throws DAOException {
        try (AutoClosable autoClosable = new AutoClosable()){
            return queryUserId(user, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
           throw new DAOException(exception);
        }
    }

    private int queryUserId(UserCredentials credentials, AutoClosable autoClosable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add((Closeable)con);

        String sql = "SELECT `user_id` FROM `user` WHERE `name` = ? AND `password_hash` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add((Closeable) preSt);

        preSt.setString(1, credentials.getName());
        preSt.setString(2, credentials.getPasswordHash());

        ResultSet rs = preSt.executeQuery();
        autoClosable.add((Closeable) rs);

        if (rs.next()) {
            return rs.getInt("user_id");
        } else {
            throw new DAOException("No such user");
        }
    }
}
