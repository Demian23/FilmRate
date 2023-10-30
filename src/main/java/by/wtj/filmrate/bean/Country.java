package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class Country {
    int countryID;
    public TextEntity text;
    public LocalisedText localisedText;
}
