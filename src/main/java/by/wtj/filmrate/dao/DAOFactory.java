package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.dao.connectionpool.ConnectionCredentials;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.impl.SQLFilmDAO;
import by.wtj.filmrate.dao.impl.SQLTranslationDAO;
import by.wtj.filmrate.dao.impl.SQLUserDAO;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.*;

public class DAOFactory {
    @Getter
    private static final DAOFactory instance = new DAOFactory();

    private final Map<Access, UserDAO> userDAOWithDifferentAccess;
    private final Map<Access, FilmDAO> filmDAOWithDifferentAccess;
    private final Map<Access, TranslationDAO> translationDAOWithDifferentAccess;

    private DAOFactory(){
        try {
            ConnectionPool.getInstance().initPoolData();
        }catch(ConnectionPoolException e){
            throw new RuntimeException(e);
        }
        userDAOWithDifferentAccess = new HashMap<>();
        userDAOWithDifferentAccess.put(Access.App, new SQLUserDAO(Access.App));
        userDAOWithDifferentAccess.put(Access.User, new SQLUserDAO(Access.User));
        userDAOWithDifferentAccess.put(Access.Admin, new SQLUserDAO(Access.Admin));
        filmDAOWithDifferentAccess = of(
                Access.App, new SQLFilmDAO(Access.App),
                Access.User, new SQLFilmDAO(Access.User),
                Access.Admin, new SQLFilmDAO(Access.Admin)
        );
        translationDAOWithDifferentAccess = of(
                Access.App, new SQLTranslationDAO(Access.App),
                Access.User, new SQLTranslationDAO(Access.User),
                Access.Admin, new SQLTranslationDAO(Access.Admin)
        );
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
