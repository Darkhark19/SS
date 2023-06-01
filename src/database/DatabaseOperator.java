package database;

import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        String sql = "INSERT INTO user_roles(username,role) VALUES(?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, role.getDescription());
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
        List<Role> roleList = new LinkedList<>();
        while(rs.next()){
            roleList.add(Role.valueOf(rs.getString("role")));
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
    public Role getRole(String roleId) throws SQLException {
        String sql = "SELECT * FROM roles WHERE role=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, roleId);
        ResultSet rs = pstmt.executeQuery();
        if(!rs.next()){
            return null;
        }
        return Role.valueOf(rs.getString("role"));
    }
    public void createPermission(Role role, Resource res, Operation op) throws SQLException {
        String sql = "INSERT INTO permissions(role,resource,operation) VALUES(?,?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, role.getDescription());
        pstmt.setString(2, res.getDescription());
        pstmt.setString(3, op.getDescription());
        pstmt.executeUpdate();
    }

    public void deletePermission(Role role, Resource res, Operation op) throws SQLException {
        String sql = "DELETE FROM permissions WHERE role=? AND resource=? AND operation=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, role.getDescription());
        pstmt.setString(2, res.getDescription());
        pstmt.setString(3, op.getDescription());
        pstmt.executeUpdate();
    }

    public Map<Resource,List<Operation>> getPermissions(Role role) throws SQLException {
        String sql = "SELECT * FROM permissions WHERE role=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, role.getDescription());
        ResultSet rs = pstmt.executeQuery();
        Map<Resource,List<Operation>> permissions = new HashMap<>();
        while(rs.next()){
            Operation op =Operation.getOperation(rs.getString("operation"));
            String  resource = rs.getString("resource");
            Resource res = Resource.getResource(resource);
            List<Operation> ops = permissions.get(res);
            if(ops == null){
                ops = new LinkedList<>();
            }
            ops.add(op);
            permissions.put(res,ops);
        }
        return permissions;
    }

    public static void main(String[] args) throws Exception {
        DatabaseOperator db = new DatabaseOperator();
//        Role admin = Role.ADMIN;
//        Role user = Role.USER;
//        db.createPermission(Role.USER, Resource.PAGES, Operation.READ);
//        db.createPermission(Role.USER, Resource.FOLLOWERS, Operation.WRITE);
//        db.createPermission(Role.USER, Resource.FOLLOWERS, Operation.PUT);
//
//
//        db.createPermission(admin, Resource.PAGES, Operation.WRITE);
//        db.createPermission(admin, Resource.USERS, Operation.WRITE);
//        db.createPermission(admin, Resource.CHANGE_USERS, Operation.READ);
//        db.createPermission(admin, Resource.CHANGE_USERS, Operation.WRITE);
        db.createAccount("root", PasswordUtils.hashPassword("1234"));
    }



}


