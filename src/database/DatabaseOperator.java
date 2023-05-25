package database;

import database.exceptions.AccountNotFountException;
import models.Account;
import models.Role;
import models.Roles;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseOperator {
    Connection connection;

    public DatabaseOperator() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void createAccount(String name, String hashedPwd) throws SQLException {

        String sql = "INSERT INTO accounts(name,password) VALUES(?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, hashedPwd);
        pstmt.executeUpdate();

        String sql2 = "INSERT INTO user_tries(username) VALUES(?)";
        PreparedStatement pstmt2 = connection.prepareStatement(sql2);
        pstmt2.setString(1, name);
        pstmt2.executeUpdate();
    }

    public void deleteAccount(String name) throws SQLException, AccountNotFountException {
        String sql = "DELETE FROM accounts WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }
        String sql2 = "DELETE FROM user_tries WHERE username=?";
        PreparedStatement pstmt2 = connection.prepareStatement(sql2);
        pstmt2.setString(1, name);
        pstmt2.executeUpdate();
    }

    public void setLock(String name, boolean lock) throws SQLException, AccountNotFountException {
        String sql = "UPDATE accounts SET locked=? WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setBoolean(1, lock);
        pstmt.setString(2, name);
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }
    }

    public void setLoggedIn(String name, boolean loggedIn) throws SQLException, AccountNotFountException {
        String sql = "UPDATE accounts SET logged_in=? WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setBoolean(1, loggedIn);
        pstmt.setString(2, name);
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }
    }

    public Account getAccount(String name) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        return DatabaseMapper.mapToAccount(rs);
    }

    public int getAccountTries(String username) throws SQLException, AccountNotFountException {
        String sql = "SELECT counter FROM user_tries WHERE username=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            throw new AccountNotFountException();
        }
        return rs.getInt("counter");
    }

    public void updateAccountTries(String username, int tries) throws SQLException {
        String sql = "UPDATE user_tries SET counter=? WHERE username=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, tries);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
    }

    public void changePwd(String name, String newHashedPwd) throws SQLException, AccountNotFountException {
        String sql = "UPDATE accounts SET password=? WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, newHashedPwd);
        pstmt.setString(2, name);
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }
    }

    public void createUserRole(String username, Role role) throws SQLException {
        String desc = role.getDescription();
        String sql = "INSERT INTO user_roles(username,role) VALUES(?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, role.toString());
        pstmt.executeUpdate();
    }

    public void deleteUserRole(String username, Role role) throws AccountNotFountException, SQLException {
        String sql = "DELETE FROM user_roles WHERE username=? AND roles=?";
        PreparedStatement pstmt =  connection.prepareStatement(sql);;
        pstmt.setString(1, username);
        pstmt.setString(2, role.getDescription());
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }

    }
    public Roles getUserRoles(String username) throws SQLException {
        String sql = "SELECT * FROM user_roles WHERE username=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        Array roles = rs.getArray("roles");
        List<Role> roleList = new LinkedList<>();
        for(String role : (String[]) roles.getArray()){
            roleList.add(new Role(role));
        }
        return new Roles(this.getAccount(username), roleList);
    }

    public void createRole(String role) throws SQLException {
        String sql = "INSERT INTO roles(role) VALUES(?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, role);
        pstmt.executeUpdate();
    }
    public void deleteRole(Role role) throws SQLException {
        String sql = "DELETE FROM roles WHERE role=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, role.getDescription());
        pstmt.executeUpdate();
    }

    public static void main(String[] args) throws SQLException, AccountNotFountException {
        DatabaseOperator db = new DatabaseOperator();
        db.createAccount("test", "test");
    }
}


