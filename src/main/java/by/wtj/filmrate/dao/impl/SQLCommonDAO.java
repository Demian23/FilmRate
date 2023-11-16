package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.dao.exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLCommonDAO {
    static public String retrieveUserName(int userId, Connection con, AutoClosableList list) throws SQLException, DAOException {
        String sql = "SELECT `name` FROM `user` WHERE `user_id` = ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        list.add(preSt);
        preSt.setInt(1, userId);

        ResultSet rs = preSt.executeQuery();
        list.add(rs);
        if(rs.next()){
            return rs.getString("name");
        }else{
            throw new DAOException("No such user with id: " + userId);
        }
    }
}
