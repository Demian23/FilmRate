package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.bean.UserInfoAggregation;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDAO {
    void registration(NewUser user)throws DAOException;
    Optional<User> getUser(UserCredentials user)throws DAOException;
    List<User> getAllUsers()throws DAOException;
    Map<Integer, UserInfoAggregation> getAllUsersAggregationInfo()throws DAOException;
}
