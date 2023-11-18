package by.wtj.filmrate.dao.connectionpool;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.dao.connectionpool.exception.ConnectionPoolException;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class ConnectionPoolWithSpecificAccess {
    private BlockingQueue<Connection> connections;
    private BlockingQueue<Connection> givenAwayConnections;
    ConnectionCredentials credentials;
    private final Access access;
    static private int connnectionSize;

    public ConnectionPoolWithSpecificAccess(Access access, int connectionsAmount, ConnectionCredentials credentials){
        connnectionSize = connectionsAmount;
        connections = new ArrayBlockingQueue<>(connectionsAmount);
        givenAwayConnections = new ArrayBlockingQueue<>(connectionsAmount);
        this.access = access;
        this.credentials = credentials;
    }

    public void initPoolData() throws ConnectionPoolException {
        try{
            for(int i = 0; i < connnectionSize; i++){
                Connection connection = DriverManager.getConnection(credentials.url, credentials.login, credentials.password);
                PooledConnection pooledConnection = new PooledConnection(connection, access);
                connections.add(pooledConnection);
            }
        } catch (SQLException e) {
            ConnectionPoolException connectionPoolException = new ConnectionPoolException(e);
            connectionPoolException.setMsgForUser("Can't connect to database");
            connectionPoolException.setLogMsg("Can't connect to database with access" + access.toString());
            connectionPoolException.addCauseModule(connectionPoolException.getStackTrace()[0].getModuleName()+"."+connectionPoolException.getStackTrace()[0].getMethodName());
            throw connectionPoolException;
        }
    }

    public void dispose() throws ConnectionPoolException {
        try{
            closeConnectionQueue(givenAwayConnections);
            closeConnectionQueue(connections);
        }catch(SQLException e){
            ConnectionPoolException connectionPoolException = new ConnectionPoolException(e);
            connectionPoolException.setMsgForUser("Can't close connection queue");
            connectionPoolException.setLogMsg("Can't close connection queue with access" + access.toString());
            connectionPoolException.addCauseModule(connectionPoolException.getStackTrace()[0].getModuleName()+"."+connectionPoolException.getStackTrace()[0].getMethodName());
            throw connectionPoolException;
        }
    }

    private void closeConnectionQueue(BlockingQueue<Connection> queue) throws SQLException {
        Connection connection;
        while((connection = queue.poll())!= null){
            if(!connection.getAutoCommit())
                connection.commit();
            ((PooledConnection)connection).forceClose();
        }
    }

    public Connection takeConnection() throws ConnectionPoolException {
        Connection con;
        try{
            con = connections.take();
            givenAwayConnections.add(con);
        }catch(InterruptedException e){
            ConnectionPoolException connectionPoolException = new ConnectionPoolException(e);
            connectionPoolException.setMsgForUser("Can't get connection from queue");
            connectionPoolException.setLogMsg("Error with access" + access.toString());
            connectionPoolException.addCauseModule(connectionPoolException.getStackTrace()[0].getModuleName()+"."+connectionPoolException.getStackTrace()[0].getMethodName());
            throw connectionPoolException;
        }
        return con;
    }

    private class PooledConnection implements Connection{
        private final Connection connection;

        public PooledConnection(Connection con, Access access) throws SQLException {
            connection = con;
            connection.setAutoCommit(true);
        }

        public void forceClose() throws SQLException {
            connection.close();
        }

        @Override
        public void close() throws SQLException {
            if(connection.isClosed()){
                throw new SQLException("Attempting to close already closed connection");
            }
            if(connection.isReadOnly())
                connection.setReadOnly(false);
            if(!givenAwayConnections.remove(this))
                throw new SQLException("Error deleting from the given away connections");
            if(!connections.offer(this)){
                throw new SQLException("Error return connection");
            }
        }

        @Override
        public Statement createStatement() throws SQLException {
            return connection.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return connection.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return connection.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return connection.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            connection.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return connection.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            connection.commit();
        }

        @Override
        public void rollback() throws SQLException {
            connection.rollback();
        }


        @Override
        public boolean isClosed() throws SQLException {
            return connection.isClosed();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return connection.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            connection.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return connection.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            connection.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            return connection.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            connection.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return connection.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return connection.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            connection.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return connection.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            connection.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            connection.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            return connection.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return connection.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return connection.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            connection.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            connection.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return connection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return connection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return connection.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return connection.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return connection.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return connection.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return connection.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return connection.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            connection.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            connection.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return connection.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return connection.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return connection.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return connection.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            connection.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return connection.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            connection.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            connection.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return connection.getNetworkTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return connection.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return connection.isWrapperFor(iface);
        }
    }
}
