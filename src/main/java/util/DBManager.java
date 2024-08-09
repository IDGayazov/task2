package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 */
public class DBManager {

    private static final PropertiesLoader propertiesLoader =
            new PropertiesLoader("application.properties");

    private String URL = propertiesLoader.getProperty("database.url");
    private String USERNAME = propertiesLoader.getProperty("database.username");
    private String PASSWORD = propertiesLoader.getProperty("database.password");

    public DBManager(){}

    public DBManager(String URL, String USERNAME, String PASSWORD) {
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database.
     *
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

}
