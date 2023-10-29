package by.wtj.filmrate.bean;


import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class Film {
    private int filmId;
    private int textEntityID;
    private int originalLangID;
    private String originalTitle;
    private String duration;
    private String ageRating;
    private String averageMark;
}
