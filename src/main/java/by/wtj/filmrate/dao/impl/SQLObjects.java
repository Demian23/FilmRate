package by.wtj.filmrate.dao.impl;

import by.wtj.filmrate.dao.exception.DAOException;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

@Data
public class SQLObjects implements Closeable {

    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @SneakyThrows
    @Override
    public void close() throws IOException {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }catch(SQLException ex){
            throw new DAOException(ex);
        }
    }
}
