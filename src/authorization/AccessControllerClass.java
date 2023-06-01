package authorization;

import database.DatabaseOperator;
import database.exceptions.AccessControlError;
import models.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

public class AccessControllerClass implements AccessController {

    private final DatabaseOperator db;

    public AccessControllerClass() {
        this.db = new DatabaseOperator();
    }

    @Override
    public Role newRole(String roleId) {
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
    public Role getRole(String roleId) {
        try {
            return db.getRole(roleId);
        } catch (SQLException e) {
            System.out.println("Error getting role");
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
    public List<Capability> createKey(Account user) {
        Roles userRoles = this.getRoles(user);
        try {
            Map<Resource, Set<Operation>> resultMap = new HashMap<>();
            for(Role role : userRoles.getRoles()) {
                Map<Resource, List<Operation>> permissions = db.getPermissions(role);
                for(Map.Entry<Resource,List<Operation>> e: permissions.entrySet()){
                   Set<Operation> ops = resultMap.get(e.getKey());
                   if(ops == null)
                       ops = new HashSet<>();
                   ops.addAll(e.getValue());
                   resultMap.put(e.getKey(), ops);
                }
            }
            List<Capability> result = new ArrayList<>();

            for(Map.Entry<Resource, Set<Operation>> e: resultMap.entrySet()){
                Capability cap = new Capability(user.getUsername());
                Date expire = new Date(System.currentTimeMillis()  + (10 * 60 * 1000));
                e.getValue().forEach(value -> result.add(cap.makeKey(e.getKey(), value,expire)));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkPermission(HttpServletRequest request, Resource res, Operation op, String username) throws AccessControlError {
        if(!cap.checkPermission(res, op,username)){
            throw new AccessControlError();
        }

    }
}

