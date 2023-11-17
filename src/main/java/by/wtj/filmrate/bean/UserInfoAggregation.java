package by.wtj.filmrate.bean;

import lombok.Data;

@Data
public class UserInfoAggregation {
    User user;
    boolean isBanned;
    String startBanPeriod;
    String endBanPeriod;
    int adminBannedId;
    String adminBannedName;
}
