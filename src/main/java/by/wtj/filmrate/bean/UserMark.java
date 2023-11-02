package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class UserMark {
    public static final int NO_MARK = -1;
    int filmId;
    int userId;
    String userName;
    int mark;
}
