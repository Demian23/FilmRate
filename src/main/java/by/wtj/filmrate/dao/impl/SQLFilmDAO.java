package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLFilmDAO implements FilmDAO {
    static private final ConnectionPool pool = ConnectionPool.getInstance();
    Access accessToDataBase;
    public SQLFilmDAO(Access access){accessToDataBase = access;}
    @Override
    public List<Film> getAllFilms() throws DAOException{
        try (AutoClosable autoClosable = new AutoClosable()){
            return queryAllFilms(autoClosable);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private List<Film> queryAllFilms(AutoClosable autoClosable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add((Closeable)con);

        Statement st = con.createStatement();
        autoClosable.add((Closeable) st);

        String sql = "SELECT * FROM `film`";
        ResultSet rs = st.executeQuery(sql);
        autoClosable.add((Closeable) rs);
        ArrayList<Film> resultList = new ArrayList<>();

        while(rs.next()){
            Film film = createFilm(rs);
            film.setText(TextEntityDAO.getTextEntity(con, autoClosable, rs.getInt("text_entity_id")));
            resultList.add(film);
        }
        return resultList;
    }

    private static Film createFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setFilmID(resultSet.getInt("film_id"));
        film.setDuration(resultSet.getString("duration"));
        film.setAgeRating(resultSet.getString("age_rating"));
        film.setLaunchDate(resultSet.getString("launch_date"));
        film.setUniverseID(resultSet.getInt("universe_id"));
        film.setWholeMarksAmount(resultSet.getInt("marks_amount"));
        film.setWholeMarksSum(resultSet.getInt("marks_whole_score"));
        return film;
    }

    @Override
    public CompleteFilmInfo getFilm(int filmId) throws DAOException{
        try (AutoClosable autoClosable = new AutoClosable()){
            return queryFilm(filmId, autoClosable);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }


    private CompleteFilmInfo queryFilm(int filmID, AutoClosable closable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closable.add((Closeable)con);

        String sql = "SELECT * FROM `film` WHERE `film_id` = ?";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, filmID);

        ResultSet rs = preSt.executeQuery();
        closable.add((Closeable) rs);

        if(rs.next()){
            CompleteFilmInfo completeFilmInfo = new CompleteFilmInfo();
            completeFilmInfo.setFilm(createFilm(rs));
            completeFilmInfo.getFilm().setText(
                    TextEntityDAO.getTextEntity(con, closable, rs.getInt("text_entity_id")));
            getFilmDetails(completeFilmInfo, rs);
           return completeFilmInfo;
        }else
            throw new DAOException("No such film");
    }
    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        //TODO if universe id not null get universe name
    }

    @Override
    public void setUserMarkToFilm(Film film, UserMark mark) throws DAOException {
        try(AutoClosable autoClosable = new AutoClosable()){
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add((Closeable)con);
            int oldMark = queryUserMark(con, autoClosable, mark);
            if(oldMark != UserMark.NO_MARK)
                queryUpdateUserMark(con, autoClosable, mark);
            else
                queryCreateUserMark(con, autoClosable, mark);
            addNewMarkToFilmAverage(con, autoClosable,film, mark, oldMark);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private void queryUpdateUserMark(Connection con, AutoClosable closable, UserMark mark) throws SQLException, DAOException {
        String sql = "UPDATE `m2m_user_film_mark` SET `value` = ? WHERE `user_id`= ? AND `film_id` = ?";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, mark.getMark());
        preSt.setInt(2, mark.getUserId());
        preSt.setInt(3, mark.getFilmId());

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected == 0){
            throw new DAOException("No mark was affected!");
        }
    }

    private void queryCreateUserMark(Connection con, AutoClosable closable, UserMark mark) throws DAOException, SQLException {
        String sql = "INSERT INTO `m2m_user_film_mark` (`user_id`, `film_id`, `value`)" +
                " VALUES(?, ?, ?);";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, mark.getUserId());
        preSt.setInt(2, mark.getFilmId());
        preSt.setInt(3, mark.getMark());

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected == 0){
            throw new DAOException("No mark was affected!");
        }
    }

    private void addNewMarkToFilmAverage(Connection con, AutoClosable closable, Film film, UserMark mark,
         int oldMarkValue) throws SQLException, DAOException {

        int marksAmountAddition = 1, wholeScoreAddition = mark.getMark();
        if(oldMarkValue != UserMark.NO_MARK) {
            marksAmountAddition = 0;
            wholeScoreAddition -= oldMarkValue;
        }

        String sql = "UPDATE `film` SET `marks_whole_score` = `marks_whole_score` + ?, `marks_amount` = `marks_amount` + ? WHERE `film_id`= ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, wholeScoreAddition);
        preSt.setInt(2, marksAmountAddition);
        preSt.setInt(3, mark.getFilmId());
        int rowsAffected = preSt.executeUpdate();

        if(rowsAffected != 0){
            film.setWholeMarksSum(film.getWholeMarksSum() + wholeScoreAddition);
            film.setWholeMarksAmount(film.getWholeMarksAmount() + marksAmountAddition);
        }else{
            throw new DAOException("No film was affected!");
        }
    }

    @Override
    public void getUserMarkToFilm(UserMark mark) throws DAOException {
        try(AutoClosable autoClosable = new AutoClosable()){
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add((Closeable) con);
            mark.setMark(queryUserMark(con, autoClosable, mark));
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private int queryUserMark(Connection con, AutoClosable closable, UserMark mark) throws DAOException, SQLException, ConnectionPoolException {
        String sql = "SELECT `value` FROM `m2m_user_film_mark` WHERE `user_id`= ? AND `film_id` = ?";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, mark.getUserId());
        preSt.setInt(2, mark.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add((Closeable) rs);
        if(rs.next())
            return rs.getInt("value");
        else
            return UserMark.NO_MARK;
    }


    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }

    @Override
    public void setUserCommentToFilm(UserComment comment) throws DAOException {
        try(AutoClosable autoClosable = new AutoClosable()){
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add((Closeable) con);
            if(isUserCommentExist(con, autoClosable, comment)){
                queryUpdateUserComment(con, autoClosable, comment);
            }else{
                queryCreateUserComment(con, autoClosable, comment);
            }
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private boolean isUserCommentExist(Connection con, AutoClosable closable, UserComment comment) throws SQLException, DAOException {
        boolean result = false;
        ResultSet rs = queryUserComment(con, closable, comment);
        if(rs.next())
            result = true;
        return result;
    }
    private void queryCreateUserComment(Connection con, AutoClosable closable, UserComment comment) throws SQLException, DAOException {
        String sql = "INSERT INTO `comment` (`user_id`, `film_id`, `score`, `text`, `date`)" +
                "VALUES(?, ?, ?, ?, ?)";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);

        preSt.setInt(1, comment.getUserId());
        preSt.setInt(2, comment.getFilmId());
        preSt.setInt(3, comment.getScore());
        preSt.setString(4, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        preSt.setTimestamp(5, commentDate);

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected != 0){
            queryUserCommentId(con, closable, comment);
            if(comment.getCommentId() == UserComment.NO_COMMENT_ID)
                throw new DAOException("After insertion no comment id found");
        }else {
            throw new DAOException("No comment was affected!");
        }
    }

    private void queryUserCommentId(Connection con, AutoClosable closable, UserComment comment) throws SQLException {
        String sql = "SELECT `comment_id` FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, comment.getUserId());
        preSt.setInt(1, comment.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add((Closeable) rs);
        if(rs.next())
            comment.setCommentId(rs.getInt("comment_id"));
        else
            comment.setCommentId(UserComment.NO_COMMENT_ID);
    }

    private void queryUpdateUserComment(Connection con, AutoClosable closable, UserComment comment) throws SQLException, DAOException {
        String sql = "UPDATE `comment` SET `score` = ?, `text` = ?, `date` = ? WHERE `comment_id`= ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);

        preSt.setInt(1, comment.getScore());
        preSt.setString(2, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        preSt.setTimestamp(3, commentDate);
        preSt.setInt(4, comment.getCommentId());

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected != 0){
            comment.setDate(commentDate.toString());
        }else {
            throw new DAOException("No comment was affected!");
        }
    }

    @Override
    public void getUserCommentToFilm(UserComment comment) throws DAOException {
        try(AutoClosable autoClosable = new AutoClosable()){
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add((Closeable) con);
            fillUserComment(queryUserComment(con, autoClosable, comment), comment);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private ResultSet queryUserComment(Connection con, AutoClosable closable, UserComment comment) throws DAOException, SQLException {
        String sql = "SELECT * FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add((Closeable) preSt);
        preSt.setInt(1, comment.getUserId());
        preSt.setInt(1, comment.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add((Closeable) rs);
        return rs;
    }
    private void fillUserComment(ResultSet rs, UserComment comment) throws SQLException {
        if(rs.next()){
            comment.setCommentId(rs.getInt("comment_id"));
            comment.setScore(rs.getInt("score"));
            comment.setDate(rs.getTimestamp("date").toString());
            comment.setText(rs.getString("text"));
        }else{
            comment.setCommentId(UserComment.NO_COMMENT_ID);
        }

    }
}
