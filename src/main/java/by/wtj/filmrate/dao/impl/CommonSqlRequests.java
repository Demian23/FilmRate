package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.BannedUser;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.dao.exception.DAOException;

import java.sql.*;
import java.util.Calendar;

public class CommonSqlRequests {
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

    static public String getAdminName(int adminId, Connection con, AutoClosableList closableList) throws SQLException, DAOException {
        String sql = "SELECT `user_id` FROM `admin` WHERE `admin_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closableList.add( preSt);
        preSt.setInt(1, adminId);
        ResultSet rs = preSt.executeQuery();
        closableList.add(rs);
        if(rs.next()) {
            int userId = rs.getInt("user_id");
            return getUserNameById(userId, con, closableList);
        }else{
            throw new DAOException("No such admin with id: " + adminId);
        }
    }
    static public String getUserNameById(int userId, Connection con, AutoClosableList list) throws SQLException, DAOException {
        String sql = "SELECT `name` FROM `user` WHERE `user_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        list.add(preSt);
        preSt.setInt(1, userId);
        ResultSet rs = preSt.executeQuery();
        list.add(rs);
        if(rs.next())
            return rs.getString("name");
        else{
            throw new DAOException("No such name for user_id: " + userId);
        }
    }

    static public void addTextEntity(TextEntity entity, Connection con, AutoClosableList list) throws SQLException, DAOException {
        // TODO add check if exist
        setTextEntityId(entity, con, list);
        if(entity.getTextEntityID() == 0) {
            String sql = "INSERT INTO `text_entity` (`original_text`, `original_language_id`)" +
                    "VALUES(?, ?)";

            PreparedStatement preSt = con.prepareStatement(sql);
            list.add(preSt);

            preSt.setString(1, entity.getTextEntity());
            preSt.setInt(2, entity.getOriginalLangID());

            int rowsAffected = preSt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("No entity was included");
            }
        }
    }

    static public void setTextEntityId(TextEntity entity, Connection con, AutoClosableList list) throws SQLException {
        String sql = "SELECT `text_entity_id` FROM `text_entity` WHERE `original_text` = ? AND `original_language_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        list.add(preSt);
        preSt.setString(1, entity.getTextEntity());
        preSt.setInt(2, entity.getOriginalLangID());
        ResultSet rs = preSt.executeQuery();
        list.add(rs);
        if(rs.next()){
            entity.setTextEntityID(rs.getInt("text_entity_id"));
        }
    }
}
