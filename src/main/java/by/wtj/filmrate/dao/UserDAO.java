package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.Optional;

public interface UserDAO {
    void registration(NewUser user)throws DAOException;
    Optional<User> getUser(UserCredentials user)throws DAOException;
}
