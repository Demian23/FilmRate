package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class Admin {
    User user;
    int adminId;
    public Admin(User user, int id){
        this.user = user; adminId = id;
    }

}
