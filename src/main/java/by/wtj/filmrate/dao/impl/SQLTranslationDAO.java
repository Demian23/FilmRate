package by.wtj.filmrate.dao.impl;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.SQLException;

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
}
