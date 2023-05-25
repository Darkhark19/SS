package models;

import java.util.List;

public class Roles {

    private Account user;
    private List<Role> roles;

    public Roles(Account user, List<Role> role){
        this.roles = role;
        this.user = user;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}



