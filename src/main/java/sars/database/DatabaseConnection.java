package sars.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/sars_db;";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "12345"; //

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Conexión establecida.");
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
                System.out.println("[DB] Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
