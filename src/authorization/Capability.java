package authorization;

import database.exceptions.TimeExpiredTokenError;
import models.Operation;
import models.Resource;

import java.util.Date;

public class Capability  {

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

    public boolean checkPermission(Resource resource, Operation op, String username) throws TimeExpiredTokenError {
        if(!this.username.equals(username))
            return false;
        else if ( expireTime.after(new Date()))
            throw new TimeExpiredTokenError();
        return this.resource.equals(resource) && this.operation.equals(op);
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
