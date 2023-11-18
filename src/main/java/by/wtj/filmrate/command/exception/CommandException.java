package by.wtj.filmrate.command.exception;

import by.wtj.filmrate.exception.FilmRateException;

public class CommandException extends FilmRateException {
    private static final long serialVersionUID = 1L;
    public CommandException(){super();}
    public CommandException(String message){super(message);}
    public CommandException(Exception exception){super(exception);}
    public CommandException(String message, Exception exception){super(message, exception);}

    public CommandException(FilmRateException e){msgForUser = e.getMsgForUser(); logMsg = e.getLogMsg();
        causeModule = e.getCauseModule();}
}
