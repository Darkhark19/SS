package authorization;

import database.exceptions.AccessControlError;
import models.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AccessController {

    Role newRole(String roleId);

    void setRole(Account user, Role role);

    Roles getRoles(Account user);

    void grantPermission(Role role, Resource res, Operation op);

    void revokePermission(Role role, Resource res, Operation op);

    List<Capability> createKey(Account user);

    void checkPermission(HttpServletRequest request, Resource res, Operation op, String username) throws AccessControlError;

    Role getRole(String roleId);

}