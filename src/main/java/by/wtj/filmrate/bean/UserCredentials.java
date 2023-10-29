package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class UserCredentials {
    private String name;
    private String passwordHash;
}
