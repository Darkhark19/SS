package database;

import database.exceptions.AccountNotFountException;
import models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void updateAccountTries(String username,int tries) throws SQLException {
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

    public static void main(String[] args) throws SQLException, AccountNotFountException {
        DatabaseOperator db = new DatabaseOperator();
        db.createAccount("test", "test");
    }
}


