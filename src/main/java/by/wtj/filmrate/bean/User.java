package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class User {
    int userId;
    int userRate;
    public User(int id, int rate){userId = id; userRate = rate;}
}
