package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AddNewFilm implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Film newFilm = takeNewFilm(request);
        FilmDAO filmDAO = CommandsComplementary.getFilmDAOForAccess(
                CommandsComplementary.getCurrentAccess(request.getSession()));
        try {
            filmDAO.addNewFilm(newFilm);
        }catch(DAOException e){
            throw new CommandException(e);
        }
        return JspPageName.newFilm;
    }

    private Film takeNewFilm(HttpServletRequest request) throws CommandException {
        String filmName = request.getParameter(RequestParameterName.NEW_FILM_NAME);
        String ageRate = request.getParameter(RequestParameterName.NEW_FILM_AGE_RATE);
        String duration = request.getParameter(RequestParameterName.NEW_FILM_DURATION);
        String launchDate = request.getParameter(RequestParameterName.NEW_FILM_LAUNCH_DATE);
        if(areFilmParamsValid(filmName, launchDate, duration, ageRate)){
            Film result = new Film();
            HttpSession session = request.getSession();
            int languageId = (Integer)session.getAttribute(SessionAttributes.CURRENT_LANG_ID);
            result.setText(new TextEntity());
            result.getText().setTextEntity(filmName);
            result.getText().setOriginalLangID(languageId);
            result.setDuration(duration);
            result.setLaunchDate(launchDate);
            result.setAgeRating(ageRate);
            result.setWholeMarksAmount(0);
            result.setWholeMarksSum(0);
            result.setUniverseID(0);
            result.setUniverseName(null);
            return result;
        }else{
            throw new CommandException("Invalid film params");
        }
    }

    private boolean areFilmParamsValid(String name, String launch, String duration, String ageRate){return true;}

    @Override
    public boolean isRedirect() {
        return false;
    }
}
