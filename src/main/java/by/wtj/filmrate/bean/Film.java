package by.wtj.filmrate.bean;


import lombok.Data;


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
    int wholeMarksSum;
    int wholeMarksAmount;
}
