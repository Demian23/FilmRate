package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;

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
    public int getUserId(UserCredentials user) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryUserId(user, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
           throw new DAOException(exception);
        }
    }

    private int queryUserId(UserCredentials credentials, AutoClosableList autoClosable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add(con);

        String sql = "SELECT `user_id` FROM `user` WHERE `name` = ? AND `password_hash` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add( preSt);

        preSt.setString(1, credentials.getName());
        preSt.setString(2, credentials.getPasswordHash());

        ResultSet rs = preSt.executeQuery();
        autoClosable.add( rs);

        if (rs.next()) {
            return rs.getInt("user_id");
        } else {
            throw new DAOException("No such user");
        }
    }
}
