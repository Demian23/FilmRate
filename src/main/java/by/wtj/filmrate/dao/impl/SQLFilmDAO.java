package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
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
            film.setText(CommonSqlRequests.getTextEntity(con, autoClosable, rs.getInt("text_entity_id")));
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
                    CommonSqlRequests.getTextEntity(con, closable, rs.getInt("text_entity_id")));
            getFilmDetails(completeFilmInfo, rs);
            return completeFilmInfo;
        } else {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("No film with id: " + filmID);
            throw daoException;
        }
    }

    @Override
    public void addNewFilm(Film newFilm) throws DAOException {
        try (AutoClosableList autoClosable = new AutoClosableList()) {
            queryAddFilm(newFilm, autoClosable);
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }

    private void queryAddFilm(Film newFilm, AutoClosableList list) throws ConnectionPoolException, SQLException, DAOException {
        Connection con = pool.takeConnectionWithAccess(accessToDataBase);
        list.add(con);

        CommonSqlRequests.addTextEntity(newFilm.getText(), con, list);
        CommonSqlRequests.setTextEntityId(newFilm.getText(), con, list);

        String sql = "INSERT INTO `film` (`launch_date`, `duration`, `universe_id`, `text_entity_id`, `marks_amount`, " +
                "`marks_whole_score`, `age_rating`)" +
                "VALUES(?, ?, ?, ?, ?,?,?)";

        PreparedStatement preSt = con.prepareStatement(sql);
        list.add(preSt);

        preSt.setDate(1, Date.valueOf(newFilm.getLaunchDate()));
        preSt.setTime(2, Time.valueOf(newFilm.getDuration()));

        if(newFilm.getUniverseName() == null || newFilm.getUniverseName().isEmpty())
            preSt.setNull(3, Types.INTEGER);
        else
            preSt.setInt(3, newFilm.getUniverseID());
        preSt.setInt(4, newFilm.getText().getTextEntityID());
        preSt.setInt(5, newFilm.getWholeMarksAmount());
        preSt.setInt(6, newFilm.getWholeMarksSum());
        preSt.setString(7, newFilm.getAgeRating());

        int rowsAffected = preSt.executeUpdate();
        if(rowsAffected == 0){
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't add film: " + newFilm.getText().getTextEntity());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }

    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        //TODO if universe id not null get universe name
    }




    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }
}