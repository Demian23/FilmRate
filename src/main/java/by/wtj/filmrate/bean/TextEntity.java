package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class TextEntity {
    static public final int NO_ID  = -1;
    int textEntityID;
    int originalLangID;
    String textEntity;
}
