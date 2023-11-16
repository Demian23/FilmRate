package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.*;
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
import java.util.Optional;

public class FilmDetails implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        CompleteFilmInfo completeFilmInfo = new CompleteFilmInfo();
        Access currentAccess = CommandsComplementary.getCurrentAccess(session);

        int filmId = Integer.parseInt(request.getParameter(RequestParameterName.FILM_ID));
        int userId = CommandsComplementary.getCurrentUserId(session);

        Optional<Film> film = tryRetrieveFilmByIdFromSession(session, filmId);
        if(film.isPresent()){
            completeFilmInfo.setFilm(film.get());
        }else{
            int languageId = Integer.parseInt(session.getAttribute(SessionAttributes.CURRENT_LANG_ID).toString());
            completeFilmInfo = fillFilm(currentAccess, filmId, languageId);
        }
        fillAdditionalFilmInfo(completeFilmInfo);
        fillUserMark(currentAccess, completeFilmInfo, userId);
        fillUserComment(currentAccess, completeFilmInfo, userId);
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
    private CompleteFilmInfo fillFilm(Access access, int filmId, int languageId) throws CommandException {
        CompleteFilmInfo completeFilmInfo;
        try{
            completeFilmInfo = DAOFactory.getInstance().getFilmDAO(access).getFilm(filmId);
            TranslationDAO translationDAO = DAOFactory.getInstance().getTranslationDAO(access);
            FillFilmsInUserPage.localiseFilm(translationDAO, completeFilmInfo.getFilm(), languageId);
        }catch (DAOException e){
            throw new CommandException(e);
        }
        return completeFilmInfo;
    }
    private void fillAdditionalFilmInfo(CompleteFilmInfo completeFilmInfo){

    }
    private void fillUserMark(Access access, CompleteFilmInfo completeFilmInfo, int userId) throws CommandException {
        UserMark mark = new UserMark();
        mark.setFilmId(completeFilmInfo.getFilm().getFilmID());
        mark.setUserId(userId);
        try{
            DAOFactory.getInstance().getMarkDAO().getUserMarkToFilm(mark);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        completeFilmInfo.setMark(mark);
    }
    private void fillUserComment(Access access, CompleteFilmInfo completeFilmInfo, int userId) throws CommandException {
        UserComment comment = new UserComment();
        comment.setFilmId(completeFilmInfo.getFilm().getFilmID());
        comment.setUserId(userId);
        try{
            DAOFactory.getInstance().getCommentDAO().getUserCommentToFilm(comment);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        completeFilmInfo.setComment(comment);
    }
}