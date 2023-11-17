package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.TextEntity;
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
    static public TextEntity getTextEntity(Connection con, AutoClosableList closable, int textEntityId) throws SQLException, DAOException {
        String sql = "SELECT * FROM `text_entity` WHERE `text_entity_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add( preSt);
        preSt.setInt(1, textEntityId);
        ResultSet rs = preSt.executeQuery();
        closable.add( rs);

        TextEntity entity = new TextEntity();
        if(rs.next()){
            entity.setTextEntityID(textEntityId);
            entity.setOriginalLangID(rs.getInt("original_language_id"));
            entity.setTextEntity(rs.getString("original_text"));
        }else{
            throw new DAOException("No such text entity with id: " + textEntityId);
        }
        return entity;
    }

    static public String getAdminName(Connection con, AutoClosableList closableList, int adminId) throws SQLException {
        String sql = "SELECT `user_id` FROM `admin` WHERE `admin_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closableList.add( preSt);
        preSt.setInt(1, adminId);
        ResultSet rs = preSt.executeQuery();
        closableList.add(rs);
        sql = "SELECT `name` FROM `user` WHERE `user_id` = ?;";
        preSt = con.prepareStatement(sql);
        closableList.add(preSt);
        preSt.setInt(1, rs.getInt("user_id"));
        rs = preSt.executeQuery();
        closableList.add(rs);
        return rs.getString("name");
    }
}
