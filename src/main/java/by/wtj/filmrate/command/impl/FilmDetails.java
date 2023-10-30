package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.TranslationDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class FilmDetails implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        int filmID = Integer.parseInt(request.getParameter(RequestParameterName.FILM_ID));
        HttpSession session = request.getSession();
        int languageID = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
        List<Film> films = (List<Film>)session.getAttribute(RequestParameterName.FILMS);
        CompleteFilmInfo filmInfo = new CompleteFilmInfo();
        if(films != null){
            filmInfo.film = films.stream().filter(film->filmID == film.getFilmID()).findFirst().orElse(null);
        }
        if(filmInfo.film == null){
            try{
                filmInfo = DAOFactory.getInstance().getFilmDAO().getFilm(filmID);
                TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO();
                if(languageID != filmInfo.film.getOriginalLangID())
                    filmInfo.film.setLocalisedTitle(translationDAO.getTranslationByLanguageID
                            (filmInfo.film.getTextEntityID(), languageID));
                else
                    filmInfo.film.setLocalisedTitle(filmInfo.film.getOriginalTitle());

                // TODO should clean this some time
                session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
            }catch (DAOException e){
                throw new CommandException(e);
            }
        }
        return JspPageName.filmPage;
    }

    @Override
    public boolean isRedirect() {
        // cause pass data through session
        return true;
    }
}
