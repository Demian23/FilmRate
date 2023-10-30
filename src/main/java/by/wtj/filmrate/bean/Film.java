package by.wtj.filmrate.bean;


import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class Film {
    int filmID;
    String launchDate;
    String duration;
    int universeID;
    String universeName;
    int textEntityID;
    int originalLangID;
    int localisationID;
    String localisedTitle;
    String originalTitle;
    String ageRating;
    String averageMark;
}
