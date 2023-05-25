package authorization;

import models.*;

public interface AccessController {

    Role newRole(String roleId);
    void setRole(Account user, Role role);
    Roles getRoles(Account user);
    void grantPermission(Role role, Resource res, Operation op);
    void revokePermission(Role role, Resource res, Operation op);
    Capability createKey(Role role);
    void checkPermission(Capability cap, Resource res, Operation op);

}
