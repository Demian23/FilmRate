package by.wtj.filmrate.dao.connectionPool;

public class ConnectionCredentials {
    ConnectionCredentials(String url, String login, String password){this.url = url; this.login = login; this.password = password;}
    String url;
    String login;
    String password;
}
