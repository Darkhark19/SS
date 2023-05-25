package database;

import models.Account;
import models.AccountClass;
import models.Role;
import models.Roles;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
