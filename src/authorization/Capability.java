package authorization;

import models.Operation;
import models.Resource;
import models.Role;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Capability {

    private Role role;
    private Map<Resource, Map<Operation,Date>> permissions;

    private Date expireTime;

    public Capability(Role role){
        this.role = role;
        this.permissions = new HashMap<>();
    }


    public Capability makeKey(Resource resource, Operation operation, Date expireTime){
        Map<Operation,Date> result = this.permissions.get(resource);
        if(result == null){
            result = new HashMap<>();
        }
        result.put(operation, expireTime);
        this.permissions.put(resource, result);
        return this;
    }

    public boolean checkPermission(Capability other, Resource resource, Operation op){
        return false;
    }
}
