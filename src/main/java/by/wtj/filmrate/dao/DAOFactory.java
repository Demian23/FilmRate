package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.dao.connectionpool.ConnectionCredentials;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;
import by.wtj.filmrate.dao.impl.SQLAdminDAO;
import by.wtj.filmrate.dao.impl.SQLFilmDAO;
import by.wtj.filmrate.dao.impl.SQLTranslationDAO;
import by.wtj.filmrate.dao.impl.SQLUserDAO;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.*;

public class DAOFactory {
    private static DAOFactory instance = null;
    private static ConnectionPool pool = null;

    private final Map<Access, UserDAO> userDAOWithDifferentAccess;
    private final Map<Access, FilmDAO> filmDAOWithDifferentAccess;
    private final Map<Access, TranslationDAO> translationDAOWithDifferentAccess;
    @Getter
    private final AdminDAO adminDAO;

    private DAOFactory(){
        userDAOWithDifferentAccess = of(
                Access.App, new SQLUserDAO(Access.App, pool),
                Access.User, new SQLUserDAO(Access.User, pool),
                Access.Admin, new SQLUserDAO(Access.Admin, pool)
        );
        filmDAOWithDifferentAccess = of(
                Access.App, new SQLFilmDAO(Access.App, pool),
                Access.User, new SQLFilmDAO(Access.User, pool),
                Access.Admin, new SQLFilmDAO(Access.Admin, pool)
        );
        translationDAOWithDifferentAccess = of(
                Access.App, new SQLTranslationDAO(Access.App, pool),
                Access.User, new SQLTranslationDAO(Access.User, pool),
                Access.Admin, new SQLTranslationDAO(Access.Admin, pool)
        );
        adminDAO = new SQLAdminDAO(pool);
    }

    public static DAOFactory getInstance() throws DAOException {
        if(pool == null)
            try {
                pool = ConnectionPool.getInstance();
            }catch(ConnectionPoolException e){
                throw new DAOException(e);
            }
        if(instance == null)
            instance = new DAOFactory();
        return instance;
    }

    public UserDAO getUserDAO(Access access){
        return userDAOWithDifferentAccess.get(access);
    }
    public FilmDAO getFilmDAO(Access access){
        return filmDAOWithDifferentAccess.get(access);
    }
    public TranslationDAO getTranslationDAO(Access access){
        return translationDAOWithDifferentAccess.get(access);
    }
}
