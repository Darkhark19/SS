package backend.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_NAME = "D:/Faculdade/SS/SqLite/SS.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    private static Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getInstance() {
       if(connection == null){
           new DatabaseConnection();
       }
       return connection;
    }

}
