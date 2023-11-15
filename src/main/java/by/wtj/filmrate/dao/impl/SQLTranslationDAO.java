package by.wtj.filmrate.dao.impl;
import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SQLTranslationDAO implements TranslationDAO{
    static private ConnectionPool pool = null;

    Access accessToDataBase;
    public SQLTranslationDAO(Access access, ConnectionPool poolInstance){
        accessToDataBase = access;
        if(pool == null){
            pool = poolInstance;
        }
    }

    @Override
    public void getTranslation(Translation translation) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            queryTranslation(autoClosable, translation);
        }catch (IOException | SQLException | ConnectionPoolException e){
           throw new DAOException(e);
        }
    }


    private void queryTranslation(AutoClosableList closable, Translation translation) throws DAOException, SQLException, ConnectionPoolException {
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
        }else
            throw new DAOException("No such translation available!");
    }

    @Override
    public int getOriginalLanguageID(int textEntityID) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryOriginalLanguageID(autoClosable, textEntityID);
        }catch (IOException | SQLException | ConnectionPoolException e){
            throw new DAOException(e);
        }
    }

    private int queryOriginalLanguageID(AutoClosableList closable, int textEntityID) throws DAOException, SQLException, ConnectionPoolException {
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
       }else
           throw new DAOException("No such text entity!");
   }

    @Override
    public List<Language> getAllLanguages() throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()){
            return queryAllLanguages(autoClosable);
        }catch (IOException | SQLException | ConnectionPoolException e){
            throw new DAOException(e);
        }
    }


    private List<Language> queryAllLanguages(AutoClosableList closable) throws DAOException, SQLException, ConnectionPoolException {
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
