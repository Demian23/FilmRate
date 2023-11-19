package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AddCommentsToCurrenFilm implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        int filmId = Integer.parseInt(request.getParameter(RequestParameterName.FILM_ID));
        List<UserComment> comments = retrieveCommentsForFilmId(filmId);
        request.setAttribute(RequestParameterName.COMMENTS_TO_CURRENT_FILM, comments);
        return JspPageName.filmPage;
    }

    private List<UserComment> retrieveCommentsForFilmId(int filmId) throws CommandException {
        try{
            return CommandsComplementary.getCommentDAO().getAllCommentsForFilm(filmId);
        } catch (DAOException e) {
            throw new CommandException(e);
        }
    }

    @Override
    public boolean isRedirect() {
        return false;
    }
}
