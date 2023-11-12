package by.wtj.filmrate.dao.connectionpool;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

public class DBResourceManager {
    @Getter
    private final static DBResourceManager instance = new DBResourceManager();

    static private final ResourceBundle bundle = ResourceBundle.getBundle("by.wtj.filmrate.dao.connectionpool.db", Locale.ENGLISH);


    public String getValue(String key){return bundle.getString(key);}
}
