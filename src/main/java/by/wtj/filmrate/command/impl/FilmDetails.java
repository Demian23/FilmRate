package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

public class FilmDetails implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        int filmId = Integer.parseInt(request.getParameter(RequestParameterName.FILM_ID));
        CompleteFilmInfo completeFilmInfo = new CompleteFilmInfo();
        HttpSession session = request.getSession();
        Optional<Film> filmOrEmpty = tryRetrieveFilmByIdFromSession(session, filmId);
        if(filmOrEmpty.isPresent()){
            completeFilmInfo.setFilm(filmOrEmpty.get());
            fillAdditionalFilmInfo(completeFilmInfo);
        }else{
            int languageId = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
            completeFilmInfo = fillWholeFilmInfo(filmId, languageId);
        }
        // TODO should clean this some time
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, completeFilmInfo);
        return JspPageName.filmPage;
    }

    @Override
    public boolean isRedirect() {
        // cause pass data through session
        return true;
    }
    private Optional<Film> tryRetrieveFilmByIdFromSession(HttpSession session, int filmId){
        Object sessionObject= session.getAttribute(RequestParameterName.FILMS);
        if(sessionObject != null){
            List<Film> sessionFilms = (List<Film>)sessionObject;
            return sessionFilms.stream().filter(someFilm -> someFilm.getFilmID() == filmId).findFirst();
        }
        return Optional.empty();
    }
    private CompleteFilmInfo fillWholeFilmInfo(int filmId, int languageId) throws CommandException {
        CompleteFilmInfo completeFilmInfo;
        try{
            completeFilmInfo = DAOFactory.getInstance().getFilmDAO().getFilm(filmId);
            FillFilmsInUserPage.localiseFilm(completeFilmInfo.getFilm(), languageId);
        }catch (DAOException e){
            throw new CommandException(e);
        }
        fillAdditionalFilmInfo(completeFilmInfo);
        return completeFilmInfo;
    }
    private void fillAdditionalFilmInfo(CompleteFilmInfo completeFilmInfo){

    }

}

