package by.wtj.filmrate.dao.connectionpool;

public class ConnectionCredentials {
    ConnectionCredentials(String url, String login, String password){this.url = url; this.login = login; this.password = password;}
    final String url;
    final String login;
    final String password;
}
