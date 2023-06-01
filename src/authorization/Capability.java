package authorization;

import models.Operation;
import models.Resource;

import java.util.Date;

public class Capability {

    private String username;
    private Resource resource;
    private Operation operation;
    private Date expireTime;

    public Capability(String username){
        this.username = username;
        this.expireTime = new Date();
        this.resource = null;
        operation = null;
    }


    public Capability makeKey(Resource resource, Operation operation, Date expireTime){
        this.resource = resource;
        this.operation = operation;
        this.expireTime = expireTime;
        return this;
    }

    public boolean checkPermission(Resource resource, Operation op, String username){
        if(!this.username.equals(username))
            return false;
        return this.resource.equals(resource) && this.operation.equals(op)
                && expireTime.after(new Date()) ;
    }
}
