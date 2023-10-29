package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class CompleteFilmInfo {
    int filmID;
    String launchDate;
    String duration;
    int universeID;
    String universeName;
    int textEntityID;
    int originalLangID;
    String localisedTitle;
    String originalTitle;
    String ageRating;
    String averageMark;
    //TODO add rewards, actors, director

    public void getFilmAttributes(Film film){
        filmID = film.getFilmId();
        duration = film.getDuration();
        originalTitle = film.getOriginalTitle();
        originalLangID = film.getOriginalLangID();
        ageRating = film.getAgeRating();
        averageMark = film.getAverageMark();
        textEntityID = film.getTextEntityID();
    }
}
