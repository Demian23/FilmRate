package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
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
        tryChangeMark(request);
        tryChangeComment(request);
        return JspPageName.filmPage;
    }
    private void tryChangeMark(HttpServletRequest request) throws CommandException {
        String markStringValue = request.getParameter(RequestParameterName.USER_MARK);
        if(!markStringValue.isEmpty()){
            int newMark = Integer.parseInt(markStringValue);
            HttpSession session = request.getSession();
            if(isMarkChanged(newMark, session)){
                try {
                    changeMark(newMark, session);
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

    private void changeMark(int newMark, HttpSession session) throws DAOException {
        UserMark mark = new UserMark();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        int userId = Integer.parseInt(session.getAttribute(SessionAttributes.USER_ID).toString());
        mark.setUserId(userId);
        mark.setFilmId(filmInfo.getFilm().getFilmID());
        mark.setMark(newMark);
        DAOFactory.getInstance().getFilmDAO().setUserMarkToFilm(filmInfo.getFilm(), mark);
        filmInfo.setMark(mark);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

    private void tryChangeComment(HttpServletRequest request) throws CommandException {
        String newComment = request.getParameter(RequestParameterName.USER_COMMENT);
        HttpSession session = request.getSession();
        if(!newComment.isEmpty()){
            if(isCommentChanged(newComment, session)){
                try {
                    changeComment(newComment, session);
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

    private void changeComment(String newComment, HttpSession session) throws DAOException {
        UserComment comment = new UserComment();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        int userId = Integer.parseInt(session.getAttribute(SessionAttributes.USER_ID).toString());
        comment.setUserId(userId);
        comment.setFilmId(filmInfo.getFilm().getFilmID());
        comment.setScore(0); // new comment -> score is 0
        comment.setText(newComment);
        comment.setCommentId(filmInfo.getComment().getCommentId());
        DAOFactory.getInstance().getFilmDAO().setUserCommentToFilm(comment);
        filmInfo.setComment(comment);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

}
