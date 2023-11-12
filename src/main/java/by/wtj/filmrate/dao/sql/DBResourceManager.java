package by.wtj.filmrate.dao.sql;

import lombok.Getter;

import java.util.ResourceBundle;

public class DBResourceManager {
    @Getter
    private final static DBResourceManager instance = new DBResourceManager();

    private ResourceBundle bundle = ResourceBundle.getBundle("by.wtj.filmrate.dao.sql.db");

    public String getValue(String key){return bundle.getString(key);}
}
