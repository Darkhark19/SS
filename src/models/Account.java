package models;

import java.util.List;

public interface Account {

    List<String> getRoles();
    String getUsername();

    String getPassword();

    boolean isLoggedIn();

    boolean isLocked();
}
