package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.exception.DAOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public class FillFilmsInUserPage implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        FilmDAO filmDAO = DAOFactory.getInstance().getFilmDAO();
        List<Film> films = null;
        HttpSession session = request.getSession();
        int languageID = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
        // TODO not all films only part
        try{
            // TODO check if they are in session
            films = filmDAO.getAllFilms();
            getFilmsTextEntities(films);
            localiseFilms(films, languageID);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        session.setAttribute(RequestParameterName.FILMS, films);
        return JspPageName.userPage;
    }
    private void getFilmsTextEntities(List<Film> films) throws DAOException {
        TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO();
        List<Integer> textEntitiesIDs = new ArrayList<>();
        for(Film film : films){
            textEntitiesIDs.add(film.getTextEntityID());
        }
        List<TextEntity> textEntities = translationDAO.getTextEntities(textEntitiesIDs);
        int i = 0;
        for(Film film : films){
            TextEntity entity = textEntities.get(i++);
            film.setOriginalLangID(entity.getOriginalLangID());
            film.setOriginalTitle(entity.getTextEntity());
        }
    }
    private void localiseFilms(List<Film> films, int languageID) throws DAOException {
        TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO();
        for(Film film : films) {
            if (film.getLocalisationID() != languageID) {
                if (film.getOriginalLangID() != languageID) {
                    String localisedTitle = translationDAO.getTranslationByLanguageID(film.getTextEntityID(), languageID);
                    film.setLocalisationID(languageID);
                    if (localisedTitle != null) {
                        film.setLocalisedTitle(localisedTitle);
                    } else {
                        film.setLocalisedTitle(film.getOriginalTitle());
                    }
                } else {
                    film.setLocalisedTitle(film.getOriginalTitle());
                }
            }
        }
    }
}
