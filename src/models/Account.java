package models;

public class Account{
    private String username;
    private String password;
    private boolean loggedIn;
    private boolean locked;

    public Account (String username, String password){
        this.username = username;
        this.password = password;
        this.loggedIn = false;
        this.locked = false;
    }

    public Account (String username, String password, boolean loggedIn, boolean locked){
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
        this.locked = locked;
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
