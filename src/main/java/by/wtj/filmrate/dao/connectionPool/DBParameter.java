package by.wtj.filmrate.dao.connectionPool;

public final class DBParameter {
    private DBParameter(){}
    public static final String DB_DRIVER = "db.driver";
    public static final String DB_URL = "db.url";
    public static final String DB_APP = "db.role.app";
    public static final String DB_APP_PASSWORD= "db.role.app.password";
    public static final String DB_USER= "db.role.user";
    public static final String DB_USER_PASSWORD= "db.role.user.password";
    public static final String DB_ADMIN = "db.role.admin";
    public static final String DB_ADMIN_PASSWORD= "db.role.admin.password";
    public static final String DB_POOL_SIZE_PER_ROLE = "db.poolSizePerRole";
}
