package com.ticketbooking.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * Singleton class for managing database connections
 */
public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static DBConnection instance;
    private Connection connection;

    private String url;
    private String username;
    private String password;

    private DBConnection() {
        try {
            loadProperties();
        } catch (IOException e) {
            logger.error("Failed to load database properties", e);
            // Use default values if properties file is not available
            url = "jdbc:postgresql://localhost:5432/ticketbooking";
            username = "postgres";
            password = "postgres";
        }
    }

    /**
     * Load database connection properties from config file
     */
    private void loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.warn("Unable to find database.properties, using default values");
                url = "jdbc:postgresql://localhost:5432/ticketbooking";
                username = "postgres";
                password = "postgres";
                return;
            }
            props.load(input);
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
        }
    }

    /**
     * Get singleton instance of DBConnection
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, username, password);
                logger.info("Database connection established");
            } catch (ClassNotFoundException e) {
                logger.error("PostgreSQL JDBC driver not found", e);
                throw new SQLException("PostgreSQL JDBC driver not found", e);
            } catch (SQLException e) {
                logger.error("Failed to connect to database", e);
                throw e;
            }
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}
