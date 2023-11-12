package by.wtj.filmrate.dao.connectionPool;

import by.wtj.filmrate.bean.Access;
import lombok.Getter;

import java.sql.*;
import java.util.*;

public class ConnectionPool {
    @Getter
    static private ConnectionPool instance = new ConnectionPool();
    private final String driverName;
    Map<Access, ConnectionPoolWithSpecificAccess> connectionPools;

    private ConnectionPool(){
        Locale.setDefault(Locale.ENGLISH);
        DBResourceManager dbResourceManager = DBResourceManager.getInstance();
        driverName = dbResourceManager.getValue(DBParameter.DB_DRIVER);
        String url = dbResourceManager.getValue(DBParameter.DB_URL);
        int poolSizePerRole = Integer.parseInt(dbResourceManager.getValue(DBParameter.DB_POOL_SIZE_PER_ROLE));
        connectionPools = new HashMap<>(3);
        connectionPools.put(Access.App, new ConnectionPoolWithSpecificAccess(Access.App, poolSizePerRole,
            new ConnectionCredentials(
                    url,
                    dbResourceManager.getValue(DBParameter.DB_APP),
                    dbResourceManager.getValue(DBParameter.DB_APP_PASSWORD)
                )
            )
        );
        connectionPools.put(Access.User, new ConnectionPoolWithSpecificAccess(Access.User, poolSizePerRole,
                new ConnectionCredentials(
                        url,
                        dbResourceManager.getValue(DBParameter.DB_USER),
                        dbResourceManager.getValue(DBParameter.DB_USER_PASSWORD)
                )
            )
        );
        connectionPools.put(Access.Admin, new ConnectionPoolWithSpecificAccess(Access.Admin, poolSizePerRole,
                new ConnectionCredentials(
                        url,
                        dbResourceManager.getValue(DBParameter.DB_ADMIN),
                        dbResourceManager.getValue(DBParameter.DB_ADMIN_PASSWORD)
                )
            )
        );
    }

    public void initPoolData() throws ConnectionPoolException {
        try{
           Class.forName(driverName);
           for(Map.Entry<Access, ConnectionPoolWithSpecificAccess> conPool : connectionPools.entrySet()){
               conPool.getValue().initPoolData();
           }
        } catch (ClassNotFoundException e) {
            throw new ConnectionPoolException("Database driver class not found", e);
        }
    }

    public Connection takeConnectionWithAccess(Access callerAccess) throws ConnectionPoolException {
        try {
            return connectionPools.get(callerAccess).takeConnection();
        }catch(NullPointerException e){
            throw new ConnectionPoolException("Unexpected access value: " + callerAccess.toString(), e);
        }
    }
}
