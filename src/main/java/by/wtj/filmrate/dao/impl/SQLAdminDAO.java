package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Admin;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.dao.AdminDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLAdminDAO implements AdminDAO {
    static private ConnectionPool pool = null;
    public SQLAdminDAO(ConnectionPool poolInstance){
        if(pool == null)
            pool = poolInstance;
    }
    @Override
    public Optional<Admin> getAdmin(User user) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryAdmin(user, autoClosable);
        }catch (SQLException | ConnectionPoolException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private Optional<Admin> queryAdmin(User user, AutoClosableList autoClosable) throws ConnectionPoolException, SQLException {
        Connection con = pool.takeConnectionWithAccess(Access.App);
        autoClosable.add(con);

        String sql = "SELECT `admin_id` FROM `admin` WHERE `user_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add(preSt);

        preSt.setInt(1, user.getUserId());

        ResultSet rs = preSt.executeQuery();
        autoClosable.add(rs);

        if (rs.next()) {
            return Optional.of(new Admin(user, rs.getInt("admin_id")));
        } else {
            return Optional.empty();
        }
    }


}
