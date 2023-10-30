package by.wtj.filmrate.dao.impl;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.Language;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SQLTranslationDAO implements TranslationDAO{

    static private final SQLDao sqlDao = SQLDao.getInstance();
    @Override
    public String getTranslationByLanguage(int textEntityID, String language) throws DAOException {
        return null;
    }

    @Override
    public String getTranslationByLanguageID(int textEntityID, int languageID) throws DAOException {
        try(SQLObjects obj = new SQLObjects()){
            return queryTranslationByLanguageID(textEntityID, languageID, obj);
        }catch (IOException | SQLException e){
           throw new DAOException(e);
        }
    }


    private String queryTranslationByLanguageID(int textEntityID, int languageID, SQLObjects obj) throws DAOException, SQLException {
        obj.setConnection(sqlDao.openSQLConnection());
        obj.setStatement(obj.getConnection().createStatement());
        String sql = "SELECT `translation` FROM `translation` WHERE `text_entity_id` = ? AND `language_id` = ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, textEntityID);
        obj.getPreparedStatement().setInt(2, languageID);
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next()){
            return obj.getResultSet().getString("translation");
        }else throw new DAOException("No such language/textEntityID available!");
    }

    @Override
    public int getOriginalLanguageID(int textEntityID) throws DAOException {
        try(SQLObjects obj = new SQLObjects()){
            return queryOriginalLanguageID(textEntityID, obj);
        }catch (IOException | SQLException e){
            throw new DAOException(e);
        }
    }

    private int queryOriginalLanguageID(int textEntityID, SQLObjects obj) throws DAOException, SQLException {
       obj.setConnection(sqlDao.openSQLConnection());
       obj.setStatement(obj.getConnection().createStatement());
       String sql = "SELECT `original_language_id` FROM `text_entity` WHERE `text_entity_id` = ?;";
       obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
       obj.getPreparedStatement().setInt(1, textEntityID);
       obj.setResultSet(obj.getPreparedStatement().executeQuery());
       if(obj.getResultSet().next()){
           return Integer.parseInt(obj.getResultSet().getString("original_language_id"));
       }else throw new DAOException("No such text entity!");
   }

    @Override
    public List<Language> getAllLanguages() throws DAOException {
        try(SQLObjects obj = new SQLObjects()){
            return queryAllLanguages(obj);
        }catch (IOException | SQLException e){
            throw new DAOException(e);
        }
    }


    private List<Language> queryAllLanguages(SQLObjects obj) throws DAOException, SQLException {
        List<Language> result = new ArrayList<>();
        obj.setConnection(sqlDao.openSQLConnection());
        obj.setStatement(obj.getConnection().createStatement());
        String sql = "SELECT * FROM `language`";
        obj.setResultSet(obj.getStatement().executeQuery(sql));
        while(obj.getResultSet().next()){
            Language lang = createLanguage(obj.getResultSet());
            result.add(lang);
        }
        return result;
    }

    private Language createLanguage(ResultSet rs) throws SQLException {
        Language lang = new Language();
        lang.setId(rs.getInt("language_id"));
        lang.setName(rs.getString("language_name"));
        return lang;
    }
    @Override
    public List<TextEntity> getTextEntities(List<Integer> textEntitiesIDs) throws DAOException {
        List<TextEntity> result = new ArrayList<>();
        try(SQLObjects obj = new SQLObjects()){
            obj.setConnection(sqlDao.openSQLConnection());
            String sql = "SELECT * FROM `text_entity` WHERE `text_entity_id` = ?;";
            obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
            for(Integer id : textEntitiesIDs){
                result.add(queryTextEntity(obj, id));
            }
        }catch (IOException | SQLException e){
            throw new DAOException(e);
        }
        return result;
    }

    private TextEntity queryTextEntity(SQLObjects obj, int id) throws DAOException, SQLException {
        obj.getPreparedStatement().setInt(1, id);
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        TextEntity entity = new TextEntity();
        if(obj.getResultSet().next()){
            entity.setTextEntityID(id);
            entity.setOriginalLangID(obj.getResultSet().getInt("original_language_id"));
            entity.setTextEntity(obj.getResultSet().getString("original_text"));
        }
        return entity;
    }
}
