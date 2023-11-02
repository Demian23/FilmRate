package by.wtj.filmrate.bean;

import lombok.Data;

import java.util.List;

@Data
public class CompleteFilmInfo {
    Film film;
    UserMark mark;
    UserComment comment;
    List<Person> actors;
    List<Person> directors;
    //TODO add rewards, actors, director
}
