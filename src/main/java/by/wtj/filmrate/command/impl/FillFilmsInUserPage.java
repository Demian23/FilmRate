package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.LocalisedText;
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

import java.util.List;

public class FillFilmsInUserPage implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        FilmDAO filmDAO = DAOFactory.getInstance().getFilmDAO();
        List<Film> films;
        HttpSession session = request.getSession();
        int languageID = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
        // TODO not all films only part
        try{
            // TODO check if they are in session
            films = filmDAO.getAllFilms();
            localiseFilms(films, languageID);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        session.setAttribute(RequestParameterName.FILMS, films);
        return JspPageName.userPage;
    }
    private void localiseFilms(List<Film> films, int languageID) throws DAOException {
        for(Film film : films) {
           localiseFilm(film, languageID);
        }
    }
    static public void localiseFilm(Film film, int languageId) throws DAOException {
        LocalisedText localisedText = film.getLocalisedText();
        TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO();
        TextEntity filmText = film.getText();
        if (localisedText.getLocalisedID() != languageId) {
            localisedText.setLocalisedID(languageId);
            if (filmText.getOriginalLangID() != languageId) {
                String localisedTitle = translationDAO.getTranslationByLanguageID(filmText.getTextEntityID(), languageId);
                localisedText.setLocalisedText(localisedTitle);
            }
        }
        if(localisedText.getLocalisedText() == null)
            localisedText.setLocalisedText(filmText.getTextEntity());
    }
}
