package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.dao.exception.DAOException;

public interface UserDAO {
    void registration(NewUser user)throws DAOException;
    int getUserId(UserCredentials user)throws DAOException;
}
