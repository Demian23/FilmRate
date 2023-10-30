package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.TextEntity;

import java.sql.SQLException;

public class TextEntityDAO {
    static public TextEntity getTextEntity(SQLObjects obj, int textEntityId) throws SQLException {
        String sql = "SELECT * FROM `text_entity` WHERE `text_entity_id` = ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, textEntityId);
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        TextEntity entity = new TextEntity();
        if(obj.getResultSet().next()){
            entity.setTextEntityID(textEntityId);
            entity.setOriginalLangID(obj.getResultSet().getInt("original_language_id"));
            entity.setTextEntity(obj.getResultSet().getString("original_text"));
        }
        return entity;
    }
}
