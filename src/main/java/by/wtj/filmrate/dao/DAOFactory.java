package by.wtj.filmrate.dao;

import by.wtj.filmrate.dao.impl.SQLFilmDAO;
import by.wtj.filmrate.dao.impl.SQLTranslationDAO;
import by.wtj.filmrate.dao.impl.SQLUserDAO;
import lombok.Getter;

public class DAOFactory {
    @Getter
    private static final DAOFactory instance = new DAOFactory();

    @Getter
    private final UserDAO userDAO= new SQLUserDAO();
    @Getter
    private final FilmDAO filmDAO = new SQLFilmDAO();
    @Getter
    private final TranslationDAO translationDAO = new SQLTranslationDAO();

    private DAOFactory(){}
}
