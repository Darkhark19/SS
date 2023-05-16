package models;

import java.util.List;

public class Role {

    private Account user;
    private List<Roles> roles;

    public Role(Account user, List<Roles> role){
        this.roles = role;
        this.user = user;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}



