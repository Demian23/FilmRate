package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.CommentDAO;
import by.wtj.filmrate.dao.MarkDAO;
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
        CommentDAO commentDAO = CommandsComplementary.getCommentDAO();
        MarkDAO markDAO = CommandsComplementary.getFactory().getMarkDAO();
        tryChangeMark(request, markDAO);
        tryChangeComment(request, commentDAO);
        return JspPageName.filmPage;
    }

    private void tryChangeMark(HttpServletRequest request, MarkDAO markDAO) throws CommandException {
        String markStringValue = request.getParameter(RequestParameterName.USER_MARK);
        if(!markStringValue.isEmpty()){
            int newMark = Integer.parseInt(markStringValue);
            HttpSession session = request.getSession();
            if(isMarkChanged(newMark, session)){
                try {
                    changeMark(markDAO, newMark, session);
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

    private void changeMark(MarkDAO markDAO, int newMark, HttpSession session) throws DAOException {
        UserMark mark = new UserMark();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        int userId = CommandsComplementary.getCurrentUserId(session);
        mark.setUserId(userId);
        mark.setFilmId(filmInfo.getFilm().getFilmID());
        mark.setMark(newMark);
        markDAO.setUserMarkToFilm(filmInfo.getFilm(), mark);
        filmInfo.setMark(mark);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

    private void tryChangeComment(HttpServletRequest request, CommentDAO commentDAO) throws CommandException {
        String newComment = request.getParameter(RequestParameterName.USER_COMMENT);
        HttpSession session = request.getSession();
        if(!newComment.isEmpty()){
            if(isCommentChanged(newComment, session)){
                try {
                    changeComment(commentDAO, newComment, session);
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

    private void changeComment(CommentDAO commentDAO, String newComment, HttpSession session) throws DAOException {
        UserComment comment = new UserComment();
        CompleteFilmInfo filmInfo = (CompleteFilmInfo)session.getAttribute(SessionAttributes.COMPLETE_FILM_INFO);
        comment.setUserId(CommandsComplementary.getCurrentUserId(session));
        comment.setFilmId(filmInfo.getFilm().getFilmID());
        comment.setScore(0); // new comment -> score is 0
        comment.setText(newComment);
        comment.setCommentId(filmInfo.getComment().getCommentId());
        commentDAO.setUserCommentToFilm(comment);
        filmInfo.setComment(comment);
        session.setAttribute(SessionAttributes.COMPLETE_FILM_INFO, filmInfo);
    }

}
