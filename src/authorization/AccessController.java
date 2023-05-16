package authorization;

import models.Account;
import models.Role;

public interface AccessController {

    Role newRole(String roleId);
    void setRole(Account user, Role role);
    Role[] getRoles(Account user);
    void grantPermission(Role role, Resource res, Operation op);
    void revokePermission(Role role, Resource res, Operation op);
    Capability makeKey(Role role);
    void checkPermission(Capability cap, Resource res, Operation op);

}
