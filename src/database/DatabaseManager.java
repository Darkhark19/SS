package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:";
    private static final String DB_NAME = "/home/tiago/segsof/authenticator-project/database.db";  // change to your local database path


        private static volatile DatabaseManager instance;
        private Connection connection;

        private DatabaseManager() {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL + DB_NAME);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseManager getInstance() {
            if (instance == null) {
                synchronized (DatabaseManager.class) {
                    if (instance == null) {
                        instance = new DatabaseManager();
                    }
                }
            }
            return instance;
        }

    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
        while (rs.next()) {
            System.out.println(rs.getString("health"));
        }
        conn.close();
    }
}

