package sars.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            Properties props = new Properties();
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new SQLException("No se pudo encontrar config.properties en los recursos.");
                }
                props.load(input);
            } catch (IOException e) {
                throw new SQLException("Error al leer config.properties", e);
            }

            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.user"),
                        props.getProperty("db.password")
                );
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL no encontrado.", e);
            }
        }
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}