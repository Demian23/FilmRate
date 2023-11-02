package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SQLFilmDAO implements FilmDAO {
    static private final SQLDao sqlDao = SQLDao.getInstance();
    @Override
    public List<Film> getAllFilms() throws DAOException{
        try(SQLObjects sqlObjects = new SQLObjects()){
            return queryAllFilms(sqlObjects);
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private List<Film> queryAllFilms(SQLObjects obj) throws DAOException, SQLException {
        obj.setConnection(sqlDao.openSQLConnection());
        obj.setStatement(obj.getConnection().createStatement());
        String sql = "SELECT * FROM `film`";
        obj.setResultSet(obj.getStatement().executeQuery(sql));
        ArrayList<Film> resultList = new ArrayList<>();
        while(obj.getResultSet().next()){
            Film film = createFilm(obj.getResultSet());
            setTextEntity(obj, film);
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
    private void setTextEntity(SQLObjects obj, Film film) throws SQLException {
        int textEntityId = obj.getResultSet().getInt("text_entity_id");
        film.setText(TextEntityDAO.getTextEntity(obj, textEntityId));
        film.setLocalisedText(new LocalisedText());
    }

    @Override
    public CompleteFilmInfo getFilm(int filmId) throws DAOException{
        try(SQLObjects sqlObjects = new SQLObjects()){
            return queryFilm(filmId, sqlObjects);
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }


    private CompleteFilmInfo queryFilm(int filmID, SQLObjects obj) throws DAOException, SQLException {
        obj.setConnection(sqlDao.openSQLConnection());
        String sql = "SELECT * FROM `film` WHERE `film_id` = ?";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, filmID);
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next()){
            CompleteFilmInfo completeFilmInfo = new CompleteFilmInfo();
            completeFilmInfo.setFilm(createFilm(obj.getResultSet()));
            setTextEntity(obj, completeFilmInfo.getFilm());
            getFilmDetails(completeFilmInfo, obj.getResultSet());
           return completeFilmInfo;
        }else throw new DAOException("No such film");
    }
    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        //TODO if universe id not null get universe name
    }

    @Override
    public void setUserMarkToFilm(Film film, UserMark mark) throws DAOException {
        try(SQLObjects sqlObjects = new SQLObjects()){
            sqlObjects.setConnection(sqlDao.openSQLConnection());
            UserMark oldMarkValue = new UserMark();
            if(isUserMarkExist(sqlObjects, mark, oldMarkValue)){
                queryUpdateUserMark(sqlObjects, film, mark, oldMarkValue);
            }else{
                queryCreateUserMark(sqlObjects, film, mark);
            }
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private boolean isUserMarkExist(SQLObjects obj, UserMark mark, UserMark oldMark) throws SQLException {
        boolean result = false;
        String sql = "SELECT * FROM `m2m_user_film_mark` WHERE `user_id`= ? AND `film_id` = ?";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, mark.getUserId());
        obj.getPreparedStatement().setInt(2, mark.getFilmId());
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next()){
            result = true;
            oldMark.setMark(obj.getResultSet().getInt("value"));
        }
        return result;
    }
    private void queryUpdateUserMark(SQLObjects obj, Film film, UserMark mark, UserMark oldMark) throws SQLException, DAOException {
        String sql = "UPDATE `m2m_user_film_mark` SET `value` = ? WHERE `user_id`= ? AND `film_id` = ?";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, mark.getMark());
        obj.getPreparedStatement().setInt(2, mark.getUserId());
        obj.getPreparedStatement().setInt(3, mark.getFilmId());
        int rowsAffected = obj.getPreparedStatement().executeUpdate();
        if(rowsAffected != 0){
            addNewMarkToFilmAverage(obj, film, mark, oldMark.getMark());
        } else{
            throw new DAOException("No mark was affected!");
        }
    }

    private void queryCreateUserMark(SQLObjects obj, Film film, UserMark mark) throws DAOException, SQLException {
        String sql = "INSERT INTO `m2m_user_film_mark` (`user_id`, `film_id`, `value`)" +
                " VALUES(?, ?, ?);";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, mark.getUserId());
        obj.getPreparedStatement().setInt(2, mark.getFilmId());
        obj.getPreparedStatement().setInt(3, mark.getMark());
        int rowsAffected = obj.getPreparedStatement().executeUpdate();
        if(rowsAffected != 0){
            addNewMarkToFilmAverage(obj, film, mark, UserMark.NO_MARK);
        } else{
            throw new DAOException("No mark was affected!");
        }
    }

    private void addNewMarkToFilmAverage(SQLObjects obj, Film film, UserMark mark, int oldMarkValue) throws SQLException, DAOException {
        String sql = "UPDATE `film` SET `marks_whole_score` = `marks_whole_score` + ?, `marks_amount` = `marks_amount` + ? WHERE `film_id`= ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        int marksAmountAddition = 1, wholeScoreAddition = mark.getMark();
        if(oldMarkValue != UserMark.NO_MARK) {
            marksAmountAddition = 0;
            wholeScoreAddition -= oldMarkValue;
        }
        obj.getPreparedStatement().setInt(1, wholeScoreAddition);
        obj.getPreparedStatement().setInt(2, marksAmountAddition);
        obj.getPreparedStatement().setInt(3, mark.getFilmId());
        int rowsAffected = obj.getPreparedStatement().executeUpdate();
        if(rowsAffected != 0){
            film.setWholeMarksSum(film.getWholeMarksSum() + wholeScoreAddition);
            film.setWholeMarksAmount(film.getWholeMarksAmount() + marksAmountAddition);
        }else{
            throw new DAOException("No film was affected!");
        }
    }

    @Override
    public void getUserMarkToFilm(UserMark mark) throws DAOException {
        try(SQLObjects sqlObjects = new SQLObjects()){
            queryUserMark(sqlObjects, mark);
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private void queryUserMark(SQLObjects obj, UserMark mark) throws DAOException, SQLException {
        obj.setConnection(sqlDao.openSQLConnection());
        String sql = "SELECT `value` FROM `m2m_user_film_mark` WHERE `user_id`= ? AND `film_id` = ?";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, mark.getUserId());
        obj.getPreparedStatement().setInt(2, mark.getFilmId());
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next())
            mark.setMark(obj.getResultSet().getInt("value"));
        else
            mark.setMark(UserMark.NO_MARK);
    }


    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }
    @Override
    public void setUserCommentToFilm(UserComment comment) throws DAOException {
        try(SQLObjects sqlObjects = new SQLObjects()){
            sqlObjects.setConnection(sqlDao.openSQLConnection());
            if(isUserCommentExist(sqlObjects, comment)){
                queryUpdateUserComment(sqlObjects, comment);
            }else{
                queryCreateUserComment(sqlObjects, comment);
            }
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private boolean isUserCommentExist(SQLObjects obj, UserComment comment) throws SQLException {
        boolean result = false;
        String sql = "SELECT * FROM `comment` WHERE `comment_id`= ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, comment.getCommentId());
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next())
            result = true;
        return result;
    }
    private void queryCreateUserComment(SQLObjects obj, UserComment comment) throws SQLException, DAOException {
        String sql = "INSERT INTO `comment` (`user_id`, `film_id`, `score`, `text`, `date`)" +
                "VALUES(?, ?, ?, ?, ?)";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, comment.getUserId());
        obj.getPreparedStatement().setInt(2, comment.getFilmId());
        obj.getPreparedStatement().setInt(3, comment.getScore());
        obj.getPreparedStatement().setString(4, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        obj.getPreparedStatement().setTimestamp(5, commentDate);
        int rowsAffected = obj.getPreparedStatement().executeUpdate();
        if(rowsAffected != 0){
            queryUserCommentId(obj, comment);
        }else {
            throw new DAOException("No mark was affected!");
        }
    }

    private void queryUserCommentId(SQLObjects obj, UserComment comment) throws SQLException {
        String sql = "SELECT `comment_id` FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, comment.getUserId());
        obj.getPreparedStatement().setInt(2, comment.getFilmId());
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next())
            comment.setCommentId(obj.getResultSet().getInt("comment_id"));
        else
            comment.setCommentId(UserComment.NO_COMMENT_ID);
    }

    private void queryUpdateUserComment(SQLObjects obj, UserComment comment) throws SQLException, DAOException {
        String sql = "UPDATE `comment` SET `score` = ?, `text` = ?, `date` = ? WHERE `comment_id`= ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, comment.getScore());
        obj.getPreparedStatement().setString(2, comment.getText());
        Timestamp commentDate = new Timestamp(System.currentTimeMillis());
        obj.getPreparedStatement().setTimestamp(3, commentDate);
        obj.getPreparedStatement().setInt(4, comment.getCommentId());
        int rowsAffected = obj.getPreparedStatement().executeUpdate();
        if(rowsAffected != 0){
            comment.setDate(commentDate.toString());
        }else {
            throw new DAOException("No comment was affected!");
        }
    }


    @Override
    public void getUserCommentToFilm(UserComment comment) throws DAOException {
        try(SQLObjects sqlObjects = new SQLObjects()){
            queryUserComment(sqlObjects, comment);
        }catch (SQLException | IOException exception){
            throw new DAOException(exception);
        }
    }

    private void queryUserComment(SQLObjects obj, UserComment comment) throws DAOException, SQLException {
        obj.setConnection(sqlDao.openSQLConnection());
        String sql = "SELECT `score`, `text`, `date`, `comment_id` FROM `comment` WHERE `user_id`= ? AND `film_id` = ?;";
        obj.setPreparedStatement(obj.getConnection().prepareStatement(sql));
        obj.getPreparedStatement().setInt(1, comment.getUserId());
        obj.getPreparedStatement().setInt(2, comment.getFilmId());
        obj.setResultSet(obj.getPreparedStatement().executeQuery());
        if(obj.getResultSet().next()) {
            comment.setText(obj.getResultSet().getString("text"));
            comment.setScore(obj.getResultSet().getInt("score"));
            comment.setDate(obj.getResultSet().getString("date"));
            comment.setCommentId(obj.getResultSet().getInt("comment_id"));
        }else{
            comment.setCommentId(UserComment.NO_COMMENT_ID);
        }
    }
}
