package by.wtj.filmrate.dao.exception;

import by.wtj.filmrate.exception.FilmRateException;

public class DAOException extends FilmRateException {
    private static final long serialVersionUID = 1L;
    public DAOException(){super();}
    public DAOException(String message){super(message);}
    public DAOException(Exception exception){super(exception);}
    public DAOException(String message, Exception exception){super(message, exception);}

    public DAOException(FilmRateException e){msgForUser = e.getMsgForUser(); logMsg = e.getLogMsg();
        causeModule = e.getCauseModule();}
}
