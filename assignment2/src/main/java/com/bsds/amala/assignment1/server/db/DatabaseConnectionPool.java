package com.bsds.amala.assignment1.server.db;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {

    // Read RDS connection information from the environment
    private static final String DB_NAME = "bsds";
    private static final String DB_USER = "bsds";
    private static final String DB_PASSWORD = "password";
    private static final String DB_HOST = "bsds.cpkn5gcvz0bw.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + PORT + "/" + DB_NAME + "?user=" + DB_USER
            + "&password=" + DB_PASSWORD;

    private final DataSource datasource;

    public DatabaseConnectionPool() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(JDBC_URL);
        poolProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setDefaultAutoCommit(true);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxActive(60);
        poolProperties.setInitialSize(60);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMinIdle(4);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        datasource = new DataSource();
        datasource.setPoolProperties(poolProperties);
    }

    public Connection getConnection() {
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
