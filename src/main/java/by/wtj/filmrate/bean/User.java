package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class User {
    int userId;
    int userRate;
    String name;
    public User(int id, int rate, String uName){userId = id; userRate = rate; name = uName;}
}
