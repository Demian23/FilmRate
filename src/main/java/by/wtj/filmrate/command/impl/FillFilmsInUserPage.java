package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.exception.DAOException;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

public class FillFilmsInUserPage implements Command {
    @Override
    public boolean isRedirect(){return false;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        FilmDAO filmDAO = DAOFactory.getInstance().getFilmDAO();
        List<Film> films = null;
        try{
             films = filmDAO.getAllFilms();
        }catch(DAOException e){
            throw new CommandException(e);
        }
        request.setAttribute(RequestParameterName.FILMS, films);
        return JspPageName.userPage;
    }
}
