package webpatterns.persistence;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

public class MySQLDao {
    private Properties properties;
    @Getter
    private String propertiesFile;
    private Connection conn;

    public MySQLDao(Connection conn){
        this.conn = conn;
    }

    public MySQLDao(String propertiesFilename){
        properties = new Properties();
        try {
            // Get the path to the specified properties file
            String rootPath = Thread.currentThread().getContextClassLoader().getResource(propertiesFilename).getPath();
            // Load in all key-value pairs from properties file
            properties.load(new FileInputStream(rootPath));
            propertiesFile = propertiesFilename;
        }catch(IOException e){
            System.out.println("An exception occurred when attempting to load properties from \"" + propertiesFilename + "\": " + e.getMessage());
        }
    }

    public Connection getConnection(){
        if(conn != null){
            return conn;
        }

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password", "");
        try{
            Class.forName(driver);

            try{
                return DriverManager.getConnection(url+database, username, password);
            }catch(SQLException e){
                System.out.println(LocalDateTime.now() + ": An SQLException  occurred while trying to connect to the " + url +
                        "database.");
                System.out.println("Error: " + e.getMessage());
            }
        }catch(ClassNotFoundException e){
            System.out.println(LocalDateTime.now() + ": A ClassNotFoundException occurred while trying to load the MySQL driver.");
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void freeConnection(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": An SQLException occurred while trying to close the " +
                    "database connection" +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
