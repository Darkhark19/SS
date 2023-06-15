package authorization;

import models.Operation;
import models.Resource;

import java.util.Date;
import java.util.List;

public class Capability {

    private Resource resource;
    private List<Operation> operations;
    private Date expireTime;

    public Capability(Resource resource, List<Operation> operations) {
        this.expireTime = new Date();
        this.resource = resource;
        this.operations = operations;
    }

    public Capability(Resource resource, List<Operation> operations, Date expireTime) {
        this.expireTime = expireTime;
        this.resource = resource;
        this.operations = operations;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperation(List<Operation> operations) {
        this.operations = operations;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
