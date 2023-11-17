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
        } else
            throw new DAOException("No such film");
    }

    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        //TODO if universe id not null get universe name
    }




    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }
}