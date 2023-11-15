package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SetUserMarkAndComment implements Command {
    @Override
    public boolean isRedirect() {
        return true;
    }
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        FilmDAO filmDAO = getFilmDAO(request);
        tryChangeMark(request, filmDAO);
        tryChangeComment(request, filmDAO);
        return JspPageName.filmPage;
    }

    FilmDAO getFilmDAO(HttpServletRequest request) throws CommandException {
        DAOFactory factory;
        try{
            factory = DAOFactory.getInstance();
        }catch (DAOException e){
            throw new CommandException(e);
        }
        HttpSession session = request.getSession();
        Object accessInSession = session.getAttribute(SessionAttributes.CURRENT_ACCESS);
        Access access = accessInSession != null ? (Access) accessInSession : Access.App;
        return factory.getFilmDAO(access);
    }
    private void tryChangeMark(HttpServletRequest request, FilmDAO filmDAO) throws CommandException {
        String markStringValue = request.getParameter(RequestParameterName.USER_MARK);
        if(!markStringValue.isEmpty()){
            int newMark = Integer.parseInt(markStringValue);
            HttpSession session = request.getSession();
            if(isMarkChanged(newMark, session)){
                try {
                    changeMark(filmDAO, newMark, session);
                }catch (DAOException e){
                    throw new CommandException(e);
                }
            }

        }
    }
    private boolean isMarkChanged(int newMark, HttpSession session){
        CompleteFilmInfo completeFilmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        int oldMark = completeFilmInfo.getMark().getMark();
        return oldMark != newMark;
    }

    private void changeMark(FilmDAO filmDAO, int newMark, HttpSession session) throws DAOException {
        UserMark mark = new UserMark();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        int userId = CommandsComplementary.getCurrentUserId(session);
        mark.setUserId(userId);
        mark.setFilmId(filmInfo.getFilm().getFilmID());
        mark.setMark(newMark);
        filmDAO.setUserMarkToFilm(filmInfo.getFilm(), mark);
        filmInfo.setMark(mark);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

    private void tryChangeComment(HttpServletRequest request, FilmDAO filmDAO) throws CommandException {
        String newComment = request.getParameter(RequestParameterName.USER_COMMENT);
        HttpSession session = request.getSession();
        if(!newComment.isEmpty()){
            if(isCommentChanged(newComment, session)){
                try {
                    changeComment(filmDAO, newComment, session);
                }catch (DAOException e){
                    throw new CommandException(e);
                }
            }
        }
    }

    private boolean isCommentChanged(String newComment, HttpSession session) {
        String oldComment= ((CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO)).getComment().getText();
        return oldComment == null || !oldComment.equals(newComment);
    }

    private void changeComment(FilmDAO filmDAO, String newComment, HttpSession session) throws DAOException {
        UserComment comment = new UserComment();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        comment.setUserId(CommandsComplementary.getCurrentUserId(session));
        comment.setFilmId(filmInfo.getFilm().getFilmID());
        comment.setScore(0); // new comment -> score is 0
        comment.setText(newComment);
        comment.setCommentId(filmInfo.getComment().getCommentId());
        filmDAO.setUserCommentToFilm(comment);
        filmInfo.setComment(comment);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

}
