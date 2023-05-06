package models;

public interface Account {
    String getUsername();

    String getPassword();

    boolean isLoggedIn();

    boolean isLocked();

    String getJWT(String id);
    void setLoggedIn(boolean b);

    void setLocked(boolean b);

    void clearPassword();

    Account clone();

}
