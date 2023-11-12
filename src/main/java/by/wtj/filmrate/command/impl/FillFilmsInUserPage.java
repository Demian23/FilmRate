package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.*;
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

import java.util.List;

public class FillFilmsInUserPage implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        int languageID = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
        Object accessInSession = session.getAttribute(SessionAttributes.CURRENT_ACCESS);
        Access access = accessInSession != null ? (Access) accessInSession : Access.App;

        FilmDAO filmDAO = DAOFactory.getInstance().getFilmDAO(access);
        TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO(access);
        List<Film> films;
        // TODO not all films only part
        try{
            // TODO check if they are in session
            films = filmDAO.getAllFilms();
            localiseFilms(translationDAO, films, languageID);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        session.setAttribute(RequestParameterName.FILMS, films);
        return JspPageName.userPage;
    }
    private void localiseFilms(TranslationDAO translationDAO, List<Film> films, int languageID) throws DAOException {
        for(Film film : films) {
           localiseFilm(translationDAO, film, languageID);
        }
    }
    static public void localiseFilm(TranslationDAO translationDAO, Film film, int languageId) throws DAOException {
        LocalisedText localisedText = film.getLocalisedText();
        TextEntity filmText = film.getText();
        if (localisedText.getLocalisedID() != languageId) {
            if (filmText.getOriginalLangID() != languageId) {
                Translation translation = new Translation();
                translation.setTextEntityId(filmText.getTextEntityID());
                translation.setLanguageId(languageId);
                translationDAO.getTranslation(translation);
                localisedText.setLocalisedText(translation.getTranslation());
            }
        }
        if(localisedText.getLocalisedText() == null)
            localisedText.setLocalisedText(filmText.getTextEntity());
    }
}
