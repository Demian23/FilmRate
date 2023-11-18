package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDAO {
    void registration(NewUser user)throws DAOException;
    Optional<UserWithBan> getUser(UserCredentials user)throws DAOException;
    List<UserWithBan> getAllUsers()throws DAOException;
    BannedUser banUser(int userId, int adminId) throws DAOException;
    boolean takeOffBan(int userId) throws DAOException;
}
