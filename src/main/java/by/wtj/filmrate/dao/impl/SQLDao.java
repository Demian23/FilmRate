package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.dao.exception.DAOException;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class SQLDao {

    static private SQLDao instance = null;
    @Getter
    @Setter
    private String url, login, password;
    static public SQLDao getInstance(){
        if(instance == null){
            instance = new SQLDao();
            File credentialsFile = new File("/Users/egor/work/java/WTJ/FilmRate/src/main/resources/sql_credentials");
            Scanner scan;
            try{
                scan = new Scanner(credentialsFile);
            }catch (FileNotFoundException ex){
                throw new RuntimeException(ex);
            }
            instance.setUrl(scan.nextLine());
            instance.setLogin(scan.nextLine());
            instance.setPassword(scan.nextLine());
        }
        return instance;
    }
    public Connection openSQLConnection() throws DAOException {
        Connection con;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, login, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new DAOException(e);
        }
        return con;
    }
}
