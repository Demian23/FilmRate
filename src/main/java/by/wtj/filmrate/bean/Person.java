package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class Person {
    int personID;
    int countryID;
    String countryName;
    public TextEntity text;
    public LocalisedText localisedText;
    String birth;
    String death;
}
