package com.juli.microservice.config;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DataBase {

    public DataBase(){
        
    }

    public static Pool createMySQLPool(final Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("127.0.0.1")
                .setDatabase("todos")
                .setUser("root")
                .setPassword("my-secret-pw");
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        return MySQLPool.pool(vertx, connectOptions, poolOptions);
    }
    
}
