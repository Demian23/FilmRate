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

        checkFilmParams(filmName, launchDate, duration, ageRate);

        int lang = (int)request.getSession().getAttribute(SessionAttributes.CURRENT_LANG_ID);
        return fillFilm(filmName, launchDate, duration, ageRate, lang);
    }

    private Film fillFilm(String filmName, String launchDate, String duration, String ageRate, int lang){
        Film result = new Film();
        result.setText(new TextEntity());
        result.getText().setTextEntity(filmName);
        result.getText().setOriginalLangID(lang);
        result.setDuration(duration);
        result.setLaunchDate(launchDate);
        result.setAgeRating(ageRate);
        result.setWholeMarksAmount(0);
        result.setWholeMarksSum(0);
        result.setUniverseID(0);
        result.setUniverseName(null);
        return result;
    }

    private void checkFilmParams(String name, String launch, String duration, String ageRate) throws CommandException {
        CommandsComplementary.checkEmptyValue(name, "Film name");
        CommandsComplementary.checkTimeFilmParam(launch, "uuuu-MM-dd", "Film launch date");
        CommandsComplementary.checkTimeFilmParam(duration, "HH:mm:ss", "Film duration");
        CommandsComplementary.checkEmptyValue(ageRate, "Film age rate");
        if(ageRate.length() > 5){
            CommandException commandException = new CommandException();
            commandException.setMsgForUser("Wrong age rate value: " + ageRate);
            throw commandException;
        }
    }


    @Override
    public boolean isRedirect() {
        return false;
    }
}
