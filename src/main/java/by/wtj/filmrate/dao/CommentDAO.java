package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface CommentDAO {
    void setUserCommentToFilm(UserComment comment) throws DAOException;
    void getUserCommentToFilm(UserComment comment) throws DAOException;

    List<UserComment> getAllCommentsForFilm(int filmId)throws DAOException;
}
