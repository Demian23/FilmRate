package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.UserComment;
import by.wtj.filmrate.dao.CommentDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLCommentDAO implements CommentDAO {
    static private ConnectionPool pool = null;
    public SQLCommentDAO(ConnectionPool poolInstance){
        if(pool == null)
            pool = poolInstance;
    }
    @Override
    public void setUserCommentToFilm(UserComment comment) throws DAOException {
        try(AutoCloseableList autoClosable = new AutoCloseableList()){
            Connection con = pool.takeConnectionWithAccess(Access.User);
            autoClosable.add(con);
            if(isUserCommentExist(con, autoClosable, comment)){
                queryUpdateUserComment(con, autoClosable, comment);
            }else{
                queryCreateUserComment(con, autoClosable, comment);
            }
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private boolean isUserCommentExist(Connection con, AutoCloseableList closable, UserComment comment) throws SQLException, DAOException {
        boolean result = false;
        ResultSet rs = queryUserComment(con, closable, comment);
        if(rs.next())
            result = true;
        return result;
    }
    private void queryCreateUserComment(Connection con, AutoCloseableList closable, UserComment comment) throws SQLException, DAOException {
        String sql = "INSERT INTO `comment` (`user_id`, `film_id`, `score`, `text`, `date`)" +
                "VALUES(?, ?, ?, ?, ?)";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);

        preSt.setInt(1, comment.getUserId());
        preSt.setInt(2, comment.getFilmId());
        preSt.setInt(3, comment.getScore());
        preSt.setString(4, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        preSt.setTimestamp(5, commentDate);

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected != 0){
            queryUserCommentId(con, closable, comment);
            if(comment.getCommentId() == UserComment.NO_COMMENT_ID) {
                DAOException daoException = new DAOException();
                daoException.setMsgForUser("Can't add " + comment.getText());
                daoException.setLogMsg("No comment_id after inserting comment: " + comment.getText());
                daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
                throw new DAOException("");
            }
        }else {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't add " + comment.getText());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }

    private void queryUserCommentId(Connection con, AutoCloseableList closable, UserComment comment) throws SQLException {
        String sql = "SELECT `comment_id` FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, comment.getUserId());
        preSt.setInt(2, comment.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add(rs);
        if(rs.next())
            comment.setCommentId(rs.getInt("comment_id"));
        else
            comment.setCommentId(UserComment.NO_COMMENT_ID);
    }

    private void queryUpdateUserComment(Connection con, AutoCloseableList closable, UserComment comment) throws SQLException, DAOException {
        String sql = "UPDATE `comment` SET `score` = ?, `text` = ?, `date` = ? WHERE `comment_id`= ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);

        preSt.setInt(1, comment.getScore());
        preSt.setString(2, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        preSt.setTimestamp(3, commentDate);
        preSt.setInt(4, comment.getCommentId());

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected != 0){
            comment.setDate(commentDate.toString());
        }else {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't update " + comment.getText());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }

    @Override
    public void getUserCommentToFilm(UserComment comment) throws DAOException {
        try(AutoCloseableList autoClosable = new AutoCloseableList()){
            Connection con = pool.takeConnectionWithAccess(Access.User);
            autoClosable.add(con);
            ResultSet rs = queryUserComment(con, autoClosable, comment);
            if(rs.next())
                fillUserComment(rs, comment);
            else
               comment.setCommentId(UserComment.NO_COMMENT_ID);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }


    private ResultSet queryUserComment(Connection con, AutoCloseableList closable, UserComment comment) throws DAOException, SQLException {
        String sql = "SELECT * FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, comment.getUserId());
        preSt.setInt(2, comment.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add(rs);
        return rs;
    }
    private void fillUserComment(ResultSet rs, UserComment comment) throws SQLException {
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setScore(rs.getInt("score"));
        comment.setDate(rs.getTimestamp("date").toString());
        comment.setText(rs.getString("text"));
        comment.setFilmId(rs.getInt("film_id"));
        comment.setUserId(rs.getInt("user_id"));
    }

    @Override
    public List<UserComment> getAllCommentsForFilm(int filmId) throws DAOException {
        try(AutoCloseableList autoClosable = new AutoCloseableList()){
            return queryAllCommentsForFilm(filmId, autoClosable);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private List<UserComment> queryAllCommentsForFilm(int filmId, AutoCloseableList autoClosable) throws ConnectionPoolException, SQLException, DAOException {
        List<UserComment> result = new ArrayList<>();
        Connection con = pool.takeConnectionWithAccess(Access.User);
        autoClosable.add(con);

        String sql = "SELECT * FROM `comment` WHERE `film_id` = ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add(preSt);
        preSt.setInt(1, filmId);

        ResultSet rs = preSt.executeQuery();
        autoClosable.add(rs);
        while(rs.next()){
            UserComment userComment = new UserComment();
            fillUserComment(rs, userComment);
            if(userComment.getCommentId() != UserComment.NO_COMMENT_ID) {
                userComment.setUserName(CommonSqlRequests.retrieveUserName(userComment.getUserId(), con, autoClosable));
                result.add(userComment);
            }
        }
        return result;
    }

}
