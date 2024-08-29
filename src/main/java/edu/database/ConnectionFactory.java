package edu.database;

import edu.database.exception.DatabaseConnectionException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@UtilityClass
public class ConnectionFactory {
    public static Connection getConnection() {
        Properties props = new Properties();

        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(input);

            Class.forName(props.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    props.getProperty("url"),
                    props.getProperty("username"),
                    props.getProperty("password")
            );
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
