package models;

import java.util.List;

public interface Account {

    void addRole(String role);
    List<String> getRoles();
    String getUsername();

    String getPassword();

    boolean isLoggedIn();

    boolean isLocked();

    void setLoggedIn(boolean loggedIn);

    void setLocked(boolean locked);

    void createToken();

    String getToken();

    Account clone();
}
