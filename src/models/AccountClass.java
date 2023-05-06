package models;

import authenticator.utils.JWTUtils;

public class AccountClass implements Account, Cloneable {
    private final String username;
    private String password;
    private boolean loggedIn;
    private boolean locked;

    public AccountClass(String username, String password, boolean loggedIn, boolean locked) {
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
        this.locked = locked;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void setLoggedIn(boolean b) {
        this.loggedIn = b;
    }

    @Override
    public void setLocked(boolean b) {
        this.locked = b;
    }

    @Override
    public void clearPassword() {
        this.password = null;
    }

    @Override
    public String getJWT(String id) {
        return JWTUtils.createJWT(this.username,id);
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
