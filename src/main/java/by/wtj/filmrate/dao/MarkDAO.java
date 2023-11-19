package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface MarkDAO {
    void setUserMarkToFilm(Film film, UserMark mark) throws DAOException;
    void getUserMarkToFilm(UserMark mark) throws DAOException;
    List<UserMark> getAllMarksForFilm(int filmId)throws DAOException;
}
