package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static authenticator.AuthenticatorClass.LOCAL_PATH;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:";
    private static final String DB_NAME = LOCAL_PATH + File.separator +"database.db";


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
        
}

