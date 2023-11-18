package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class UserWithBan {
    User user;
    BannedUser bannedInfo;
    boolean ban;
    public boolean isBan(){return ban;}
}
