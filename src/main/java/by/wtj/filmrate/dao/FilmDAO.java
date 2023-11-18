package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface FilmDAO {
    List<Film> getAllFilms() throws DAOException;
    CompleteFilmInfo getFilm(int filmId)throws DAOException;
    void addNewFilm(Film newFilm)throws DAOException;
}
