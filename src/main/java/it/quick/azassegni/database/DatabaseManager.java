package it.quick.azassegni.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection;

    public static void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;

            connection = DriverManager.getConnection("jdbc:sqlite:plugins/azAssegni/assegni.db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS assegni (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player TEXT NOT NULL, " +
                "amount DOUBLE NOT NULL);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveAssegno(String player, double amount) {
        String sql = "INSERT INTO assegni (player, amount) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAssegno(String player, double amount) {
        String sql = "DELETE FROM assegni WHERE player = ? AND amount = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
