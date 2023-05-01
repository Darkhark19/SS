package models;

import java.util.LinkedList;
import java.util.List;

public class AccountClass implements Account, Cloneable {
    private String username;
    private String password;
    private boolean loggedIn;
    private boolean locked;
    private List<String> roles;
    private String token;

    @Override
    public String getToken() {
        return token;
    }

    public void createToken() {
    }


    public AccountClass(String username, String password) {
        this.username = username;
        this.password = password;
        this.loggedIn = false;
        this.locked = false;
        roles = new LinkedList<>();
    }

    public AccountClass(String username, String password, boolean loggedIn, boolean locked) {
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
        this.locked = locked;
        roles = new LinkedList<>();
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public Account clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (AccountClass) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
