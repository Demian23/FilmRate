package by.wtj.filmrate.dao.impl;
import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SQLTranslationDAO implements TranslationDAO{
    static private ConnectionPool pool = null;

    private final Access accessToDataBase;
    public SQLTranslationDAO(Access access, ConnectionPool poolInstance){
        accessToDataBase = access;
        if(pool == null){
            pool = poolInstance;
        }
    }

    @Override
    public void getTranslation(Translation translation) throws DAOException {
        try (AutoCloseableList autoClosable = new AutoCloseableList()){
            queryTranslation(autoClosable, translation);
        }catch (IOException | SQLException | ConnectionPoolException e){
           throw new DAOException(e);
        }
    }


    private void queryTranslation(AutoCloseableList closable, Translation translation) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closable.add(con);

        String sql = "SELECT `translation` FROM `translation` WHERE `text_entity_id` = ? AND `language_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, translation.getTextEntityId());
        preSt.setInt(2, translation.getLanguageId());

        ResultSet rs = preSt.executeQuery();
        closable.add(rs);
        if(rs.next()){
            translation.setTranslation(rs.getString("translation"));
        }else {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("No such translation for textId: " + translation.getTextEntityId());
            throw daoException;
        }
    }

    @Override
    public int getOriginalLanguageID(int textEntityID) throws DAOException {
        try (AutoCloseableList autoClosable = new AutoCloseableList()){
            return queryOriginalLanguageID(autoClosable, textEntityID);
        }catch (IOException | SQLException | ConnectionPoolException e){
            throw new DAOException(e);
        }
    }

    private int queryOriginalLanguageID(AutoCloseableList closable, int textEntityID) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closable.add(con);

        String sql = "SELECT `original_language_id` FROM `text_entity` WHERE `text_entity_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add( preSt);
        preSt.setInt(1, textEntityID);

        ResultSet rs = preSt.executeQuery();
        closable.add( rs);
       if(rs.next()){
           return rs.getInt("original_language_id");
       }else{
           DAOException daoException = new DAOException();
           daoException.setMsgForUser("No text entity for textId: " + textEntityID);
           throw daoException;
       }
   }

    @Override
    public List<Language> getAllLanguages() throws DAOException {
        try (AutoCloseableList autoClosable = new AutoCloseableList()){
            return queryAllLanguages(autoClosable);
        }catch (IOException | SQLException | ConnectionPoolException e){
            throw new DAOException(e);
        }
    }


    private List<Language> queryAllLanguages(AutoCloseableList closable) throws SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closable.add(con);

        String sql = "SELECT * FROM `language`";
        Statement st = con.prepareStatement(sql);
        closable.add(st);

        ResultSet rs = st.executeQuery(sql);
        closable.add( rs);

        List<Language> result = new ArrayList<>();
        while(rs.next()){
            Language lang = createLanguage(rs);
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
}
