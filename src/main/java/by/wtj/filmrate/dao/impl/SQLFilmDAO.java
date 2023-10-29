package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.CompleteFilmInfo;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            resultList.add(film);
        }
        return resultList;
    }

    private static Film createFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setFilmId(resultSet.getInt("film_id"));
        film.setOriginalTitle(resultSet.getString("original_title"));
        film.setDuration(resultSet.getString("duration"));
        film.setTextEntityID(resultSet.getInt("text_entity_id"));
        film.setAgeRating(resultSet.getString("age_rating"));
        int marksAmount = resultSet.getInt("marks_amount");
        if(marksAmount != 0)
            film.setAverageMark(
                    String.format("%.2f", (double) resultSet.getInt("marks_whole_score") / marksAmount));
        else
            film.setAverageMark("0");
        return film;
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
            completeFilmInfo.getFilmAttributes(createFilm(obj.getResultSet()));
            getFilmDetails(completeFilmInfo, obj.getResultSet());
           return completeFilmInfo;
        }else throw new DAOException("No such film");
    }
    private void getFilmDetails(CompleteFilmInfo film, ResultSet rs) throws SQLException {
        film.setLaunchDate(rs.getString("launch_date"));
        film.setUniverseID(rs.getInt("universe_id"));
        //TODO if universe id not null get universe name
    }

    @Override
    public void updateFilm(CompleteFilmInfo filmInfo) {

    }
}
