package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface FilmDAO {
    List<Film> getAllFilms() throws DAOException;
    CompleteFilmInfo getFilm(int filmId)throws DAOException;
    void setUserMarkToFilm(Film film, UserMark mark) throws DAOException;
    void setUserCommentToFilm(UserComment comment) throws DAOException;
    void getUserCommentToFilm(UserComment comment) throws DAOException;
    void getUserMarkToFilm(UserMark mark) throws DAOException;
    void updateFilm(CompleteFilmInfo filmInfo);
}
