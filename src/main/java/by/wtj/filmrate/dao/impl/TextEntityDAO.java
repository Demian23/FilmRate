package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TextEntityDAO {
    static public TextEntity getTextEntity(Connection con, AutoClosable closable, int textEntityId) throws SQLException, DAOException {
        String sql = "SELECT * FROM `text_entity` WHERE `text_entity_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, textEntityId);
        ResultSet rs = preSt.executeQuery();
        closable.add((Closeable) rs);

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
}
