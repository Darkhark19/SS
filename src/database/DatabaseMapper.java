package database;

import models.Account;
import models.AccountClass;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DatabaseMapper {
    public static Account mapToAccount(ResultSet rs) throws SQLException {
        return new AccountClass(
                rs.getString("name"),
                rs.getString("password"),
                rs.getBoolean("logged_in"),
                rs.getBoolean("locked")
        );
    }

}
