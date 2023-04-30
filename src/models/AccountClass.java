package models;

import java.util.LinkedList;
import java.util.List;

public class AccountClass implements Account{
    private String username;
    private String password;
    private boolean loggedIn;
    private boolean locked;

    private List<String> roles;


    public AccountClass (String username, String password){
        this.username = username;
        this.password = password;
        this.loggedIn = false;
        this.locked = false;
        roles = new LinkedList<>();
    }

    public AccountClass (String username, String password, boolean loggedIn, boolean locked){
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
        this.locked = locked;
    }

    public void addRoles(String role){
        this.roles.add(role);
    }

    public List<String> getRoles(){
        return roles;
    }
    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public boolean isLoggedIn(){
        return this.loggedIn;
    }

    public boolean isLocked(){
        return this.locked;
    }
}
