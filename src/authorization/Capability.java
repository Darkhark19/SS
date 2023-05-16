package authorization;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Capability {

    private String username;
    private Map<Resource, List<Operation>> permissions;

    public Capability(String username){
        this.username = username;
        this.permissions = new HashMap<>();
    }




    public Capability makeKey(Resource resource, Operation operation, Date expireTime){

        return this;
    }

    public boolean checkPermission(Capability other, Resource resource, Operation op){
        return false;
    }
}
