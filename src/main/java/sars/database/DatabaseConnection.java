package sars.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    // Cambiamos el puerto a 6543 y el subdominio directo db.
    /*private static final String URL      = "jdbc:postgresql://db.amflcutdoafzipyrowit.supabase.co:6543/postgres";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "101208F@cho2026";

    /*private static final String URL      = "jdbc:postgresql://thomas.proxy.rlwy.net:17892/railway";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "btLlVZsEgyTYUmaKUJfjHIawhmyGSOhq";*/

    private static final String URL = "jdbc:postgresql://localhost:5432/sars_db;";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "12345";


    private static Connection instance;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                // Mantiene tus tres parámetros originales impecable
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Conexión en la nube establecida con éxito.");
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



