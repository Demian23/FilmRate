package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class UserComment {
    public static final int NO_COMMENT_ID = -1;
    int commentId;
    int filmId;
    int userId;
    String userName;
    int score;
    String text;
    String date;
}
