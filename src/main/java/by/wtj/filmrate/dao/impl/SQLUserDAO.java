package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;

public class SQLUserDAO implements UserDAO {

    static private final SQLDao sqlDao = SQLDao.getInstance();
    @Override
    public void registration(NewUser user) throws DAOException {

    }

    @Override
    public int getUserId(UserCredentials user) throws DAOException {
        try (SQLObjects sqlObjects = new SQLObjects()){
            return queryUserId(user, sqlObjects);
        }catch (SQLException | IOException exception){
           throw new DAOException(exception);
        }
    }

    private int queryUserId(UserCredentials credentials, SQLObjects sqlObjects) throws DAOException, SQLException {
        sqlObjects.setConnection(sqlDao.openSQLConnection());
        sqlObjects.setStatement(sqlObjects.getConnection().createStatement());
        String sql = "SELECT `user_id` FROM `user` WHERE `name` = ? AND `password_hash` = ?;";
        sqlObjects.setPreparedStatement(sqlObjects.getConnection().prepareStatement(sql));
        sqlObjects.getPreparedStatement().setString(1, credentials.getName());
        sqlObjects.getPreparedStatement().setString(2, credentials.getPasswordHash());
        sqlObjects.setResultSet(sqlObjects.getPreparedStatement().executeQuery());
        if (sqlObjects.getResultSet().next()) {
            return sqlObjects.getResultSet().getInt("user_id");
        } else {
            throw new DAOException("No such user");
        }
    }
}
