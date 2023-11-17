package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class BannedUser {
    int userId;
    String startPeriod;
    String endPeriod;
    int adminBannedId;
}
