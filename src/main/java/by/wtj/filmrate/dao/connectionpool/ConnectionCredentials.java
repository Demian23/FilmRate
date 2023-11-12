package by.wtj.filmrate.dao.connectionpool;

public class ConnectionCredentials {
    ConnectionCredentials(String url, String login, String password){this.url = url; this.login = login; this.password = password;}
    String url;
    String login;
    String password;
}
