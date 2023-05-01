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
    }

    public void deleteAccount(String name) throws SQLException, AccountNotFountException {
        String sql = "DELETE FROM accounts WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        int result = pstmt.executeUpdate();
        if (result == 0) {
            throw new AccountNotFountException();
        }

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
    public void logoutAccount(String name) throws SQLException, AccountNotFountException {
        String sql = "UPDATE accounts SET logged_in=? WHERE name=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setBoolean(1, false);
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
}


