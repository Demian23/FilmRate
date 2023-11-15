package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Admin;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.Optional;

public interface AdminDAO {
    Optional<Admin> getAdmin(User user) throws DAOException;
}
