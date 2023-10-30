package by.wtj.filmrate.bean;


import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class Film {
    TextEntity text;
    LocalisedText localisedText;
    int filmID;
    String launchDate;
    String duration;
    int universeID;
    String universeName;
    String ageRating;
    String averageMark;
}
