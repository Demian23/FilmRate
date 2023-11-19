package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AddMarksToCurrentFilmInSession implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        int filmId = Integer.parseInt(request.getParameter(RequestParameterName.FILM_ID));
        List<UserMark> marks = retrieveMarksForFilmId(filmId);
        request.setAttribute(RequestParameterName.MARKS_TO_CURRENT_FILM, marks);
        return JspPageName.filmPage;
    }

    private List<UserMark> retrieveMarksForFilmId(int filmId) throws CommandException {
        try{
            return CommandsComplementary.getFactory().getMarkDAO().
                getAllMarksForFilm(filmId);
        } catch (DAOException e) {
            throw new CommandException(e);
        }
    }



    @Override
    public boolean isRedirect() {
        return false;
    }
}
