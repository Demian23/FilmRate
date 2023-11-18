package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Film;
import by.wtj.filmrate.bean.UserMark;
import by.wtj.filmrate.dao.MarkDAO;
import by.wtj.filmrate.dao.connectionpool.ConnectionPool;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;
import by.wtj.filmrate.dao.exception.DAOException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLMarkDAO implements MarkDAO {
    static private ConnectionPool pool = null;

    public SQLMarkDAO(ConnectionPool poolInstance) {
        if (pool == null)
            pool = poolInstance;
    }
    @Override
    public void getUserMarkToFilm(UserMark mark) throws DAOException {
        try (AutoCloseableList autoClosable = new AutoCloseableList()) {
            Connection con = pool.takeConnectionWithAccess(Access.User);
            autoClosable.add(con);
            mark.setMark(queryUserMark(con, autoClosable, mark));
        } catch (SQLException | IOException | ConnectionPoolException exception) {
            throw new DAOException(exception);
        }
    }

    private int queryUserMark(Connection con, AutoCloseableList closable, UserMark mark) throws DAOException, SQLException, ConnectionPoolException {
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
    public void setUserMarkToFilm(Film film, UserMark mark) throws DAOException {
        try (AutoCloseableList autoClosable = new AutoCloseableList()) {
            Connection con = pool.takeConnectionWithAccess(Access.User);
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

    private void queryUpdateUserMark(Connection con, AutoCloseableList closable, UserMark mark) throws SQLException, DAOException {
        String sql = "UPDATE `m2m_user_film_mark` SET `value` = ? WHERE `user_id`= ? AND `film_id` = ?";
        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, mark.getMark());
        preSt.setInt(2, mark.getUserId());
        preSt.setInt(3, mark.getFilmId());

        int rowsAffected = preSt.executeUpdate();
        if (rowsAffected == 0) {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't update mark: " + mark.getMark());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }

    private void queryCreateUserMark(Connection con, AutoCloseableList closable, UserMark mark) throws DAOException, SQLException {
        String sql = "INSERT INTO `m2m_user_film_mark` (`user_id`, `film_id`, `value`)" +
                " VALUES(?, ?, ?);";

        PreparedStatement preSt = con.prepareStatement(sql);
        closable.add(preSt);
        preSt.setInt(1, mark.getUserId());
        preSt.setInt(2, mark.getFilmId());
        preSt.setInt(3, mark.getMark());

        int rowsAffected = preSt.executeUpdate();
        if (rowsAffected == 0) {
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't add mark: " + mark.getMark());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }

    private void addNewMarkToFilmAverage(Connection con, AutoCloseableList closable, Film film, UserMark mark,
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
            DAOException daoException = new DAOException();
            daoException.setMsgForUser("Can't add mark: " + mark.getMark() + "to film: " + film.getText().getTextEntity());
            daoException.setLogMsg("0 rows affected after " + preSt.toString());
            daoException.addCauseModule(daoException.getStackTrace()[0].getModuleName()+"."+daoException.getStackTrace()[0].getMethodName());
            throw daoException;
        }
    }


    public List<UserMark> getAllMarksForFilm(int filmId) throws DAOException {
        try(AutoCloseableList autoClosable = new AutoCloseableList()){
            return queryAllMarksForFilm(filmId, autoClosable);
        }catch (SQLException | IOException | ConnectionPoolException exception){
            throw new DAOException(exception);
        }
    }

    private List<UserMark> queryAllMarksForFilm(int filmId, AutoCloseableList autoClosable) throws ConnectionPoolException, SQLException, DAOException {
        List<UserMark> result = new ArrayList<>();
        Connection con = pool.takeConnectionWithAccess(Access.User);
        autoClosable.add(con);

        String sql = "SELECT * FROM `m2m_user_film_mark` WHERE `film_id` = ?;";

        PreparedStatement preSt = con.prepareStatement(sql);
        autoClosable.add(preSt);
        preSt.setInt(1, filmId);

        ResultSet rs = preSt.executeQuery();
        autoClosable.add(rs);

        while(rs.next()){
            UserMark userMark= makeUserMark(rs);
            userMark.setUserName(CommonSqlRequests.retrieveUserName(
                userMark.getUserId(), con, autoClosable));
            result.add(userMark);
        }
        return result;
    }

    private UserMark makeUserMark(ResultSet rs) throws SQLException {
        UserMark result = new UserMark();
        result.setUserId(rs.getInt("user_id"));
        result.setMark(rs.getInt("value"));
        result.setFilmId(rs.getInt("film_id"));
        return result;
    }
}
