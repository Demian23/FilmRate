package by.wtj.filmrate.dao.sql;

public class ConnectionPoolException extends Exception {
    private static final long serialVersionUID = 1L;
    public ConnectionPoolException(String msg, Exception e){
        super(msg, e);
    }
}
