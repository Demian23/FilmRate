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
    static private ConnectionPool pool = null;
    Access accessToDataBase;

    public SQLFilmDAO(Access access, ConnectionPool poolInstance) {
        accessToDataBase = access;
        if (pool == null)
            pool = poolInstance;
    }

    @Override
    public List<Film> getAllFilms() throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()) {
            return queryAllFilms(autoClosable);
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }

    private List<Film> queryAllFilms(AutoClosableList autoClosable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        autoClosable.add(con);

        Statement st = con.createStatement();
        autoClosable.add(st);

        String sql = "SELECT * FROM `film`";
        ResultSet rs = st.executeQuery(sql);
        autoClosable.add(rs);
        ArrayList<Film> resultList = new ArrayList<>();

        while (rs.next()) {
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
    public CompleteFilmInfo getFilm(int filmId) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()) {
            return queryFilm(filmId, autoClosable);
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }


    private CompleteFilmInfo queryFilm(int filmID, AutoClosableList closable) throws DAOException, SQLException, ConnectionPoolException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        closable.add(con);

        String sql = "SELECT * FROM `film` WHERE `film_id` = ?";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, filmID);

        ResultSet rs = preSt.executeQuery();
        closable.add(rs);

        if (rs.next()) {
            CompleteFilmInfo completeFilmInfo = new CompleteFilmInfo();
            completeFilmInfo.setFilm(createFilm(rs));
            completeFilmInfo.getFilm().setText(
                    TextEntityDAO.getTextEntity(con, closable, rs.getInt("text_entity_id")));
            getFilmDetails(completeFilmInfo, rs);
            return completeFilmInfo;
        } else
            throw new DAOException("No such film");
    }

    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        //TODO if universe id not null get universe name
    }

    @Override
    public void setUserMarkToFilm(Film film, UserMark mark) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()) {
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add(con);
            int oldMark = queryUserMark(con, autoClosable, mark);
            if (oldMark != UserMark.NO_MARK)
                queryUpdateUserMark(con, autoClosable, mark);
            else
                queryCreateUserMark(con, autoClosable, mark);
            addNewMarkToFilmAverage(con, autoClosable, film, mark, oldMark);
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }

    private void queryUpdateUserMark(Connection con, AutoClosableList closable, UserMark mark) throws SQLException, DAOException {
        String sql = "UPDATE `m2m_user_film_mark` SET `value` = ? WHERE `user_id`= ? AND `film_id` = ?";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, mark.getMark());
        preSt.setInt(2, mark.getUserId());
        preSt.setInt(3, mark.getFilmId());

        int rowsAffected = preSt.executeUpdate();
        if (rowsAffected == 0) {
            throw new DAOException("No mark was affected!");
        }
    }

    private void queryCreateUserMark(Connection con, AutoClosableList closable, UserMark mark) throws DAOException, SQLException {
        String sql = "INSERT INTO `m2m_user_film_mark` (`user_id`, `film_id`, `value`)" +
                " VALUES(?, ?, ?);";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, mark.getUserId());
        preSt.setInt(2, mark.getFilmId());
        preSt.setInt(3, mark.getMark());

        int rowsAffected = preSt.executeUpdate();
        if (rowsAffected == 0) {
            throw new DAOException("No mark was affected!");
        }
    }

    private void addNewMarkToFilmAverage(Connection con, AutoClosableList closable, Film film, UserMark mark,
                                         int oldMarkValue) throws SQLException, DAOException {

        int marksAmountAddition = 1, wholeScoreAddition = mark.getMark();
        if (oldMarkValue != UserMark.NO_MARK) {
            marksAmountAddition = 0;
            wholeScoreAddition -= oldMarkValue;
        }

        String sql = "UPDATE `film` SET `marks_whole_score` = `marks_whole_score` + ?, `marks_amount` = `marks_amount` + ? WHERE `film_id`= ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, wholeScoreAddition);
        preSt.setInt(2, marksAmountAddition);
        preSt.setInt(3, mark.getFilmId());
        int rowsAffected = preSt.executeUpdate();

        if (rowsAffected != 0) {
            film.setWholeMarksSum(film.getWholeMarksSum() + wholeScoreAddition);
            film.setWholeMarksAmount(film.getWholeMarksAmount() + marksAmountAddition);
        } else {
            throw new DAOException("No film was affected!");
        }
    }

    @Override
    public void getUserMarkToFilm(UserMark mark) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()) {
            Connection con = pool.takeConnectionWithAccess(accessToDataBase);
            autoClosable.add(con);
            mark.setMark(queryUserMark(con, autoClosable, mark));
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }

    private int queryUserMark(Connection con, AutoClosableList closable, UserMark mark) throws DAOException, SQLException, ConnectionPoolException {
        String sql = "SELECT `value` FROM `m2m_user_film_mark` WHERE `user_id`= ? AND `film_id` = ?";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, mark.getUserId());
        preSt.setInt(2, mark.getFilmId());

        ResultSet rs = preSt.executeQuery();
        closable.add(rs);
        if (rs.next())
            return rs.getInt("value");
        else
            return UserMark.NO_MARK;
    }


    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }
}