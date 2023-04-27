package database;

import models.Account;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMapper {
    public static Account mapToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getString("name"),
                "PASSWORD",
                rs.getBoolean("logged_in"),
                rs.getBoolean("locked")
        );
    }
}
