package authorization;

import database.DatabaseOperator;
import models.*;

import java.sql.SQLException;

public class AccessControllerClass implements AccessController{

    private final DatabaseOperator db;
    public AccessControllerClass() {
        this.db = new DatabaseOperator();
    }
    @Override
    public Role newRole(String roleId)  {
        try {
            db.createRole(roleId);
        } catch (SQLException e) {
            System.out.println("Error creating role");
            throw new RuntimeException(e);

        }
        return new Role(roleId);
    }



    @Override
    public void setRole(Account user, Role role) {
        try {
            db.createUserRole(user.getUsername(), role);
        } catch (SQLException e) {
            System.out.println("Error setting role");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Roles getRoles(Account user) {
        try {
            return db.getUserRoles(user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error getting roles");
            throw new RuntimeException(e);
        }
    }


    @Override
    public void grantPermission(Role role, Resource res, Operation op) {
        try {
            db.createPermission(role, res, op);
        } catch (SQLException e) {
            System.out.println("Error granting permission");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void revokePermission(Role role, Resource res, Operation op) {
        try {
            db.deletePermission(role, res, op);
        } catch (SQLException e) {
            System.out.println("Error revoking permission");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Capability createKey(Role role) {
        return new Capability(role);
    }

    @Override
    public void checkPermission(Capability cap, Resource res, Operation op) {

    }

    @Override
    public Role getRole(String roleId) {
        try {
            return db.getRole(roleId);
        } catch (SQLException e) {
            System.out.println("Error getting role");
            throw new RuntimeException(e);
        }
    }
}
