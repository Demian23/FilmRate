package by.wtj.filmrate.command.exception;

public class CommandException extends Exception{
    private static final long serialVersionUID = 1L;
    public CommandException(){super();}
    public CommandException(String message){super(message);}
    public CommandException(Exception exception){super(exception);}
    public CommandException(String message, Exception exception){super(message, exception);}
}
