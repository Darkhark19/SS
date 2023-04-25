package database;

import javax.ejb.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:";
    private static final String DB_NAME = "/home/tiago/segsof/authenticator-project/database.db";  // change to your local database path

    private static DatabaseConnection instance = null;
    private static Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL + DB_NAME);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return connection;
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
        while (rs.next()) {
            System.out.println(rs.getString("id"));
        }
        conn.close();
    }
}

