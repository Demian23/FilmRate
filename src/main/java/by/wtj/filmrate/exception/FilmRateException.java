package by.wtj.filmrate.exception;

public class FilmRateException extends Exception{
    private static final long serialVersionUID = 1L;
    protected String msgForUser = "";
    protected String logMsg = "";
    protected String causeModule = "";

    public FilmRateException(){super();}
    public FilmRateException(String message){super(message);}
    public FilmRateException(Exception exception){super(exception);}
    public FilmRateException(String message, Exception exception){super(message, exception);}

    public FilmRateException(FilmRateException e){super(e); msgForUser = e.getMsgForUser(); logMsg = e.logMsg; causeModule = e.causeModule;}

    public void setMsgForUser(String msg){msgForUser += msg;}
    public void setLogMsg(String msg){logMsg += msg;}
    public void addCauseModule(String cause){causeModule += cause + "->";}
    public String getLogMsg(){return logMsg;}
    public String getMsgForUser(){return msgForUser;}
    public String getCauseModule(){return causeModule;}
}
