package by.wtj.filmrate.dao.connectionpool.exception;

import by.wtj.filmrate.exception.FilmRateException;

public class ConnectionPoolException extends FilmRateException {
    private static final long serialVersionUID = 1L;
    public ConnectionPoolException(){super();}
    public ConnectionPoolException(String message){super(message);}
    public ConnectionPoolException(Exception exception){super(exception);}
    public ConnectionPoolException(String message, Exception exception){super(message, exception);}

    public ConnectionPoolException(FilmRateException e){msgForUser = e.getMsgForUser(); logMsg = e.getLogMsg();
        causeModule = e.getCauseModule();}

}
