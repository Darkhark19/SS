package authenticator;

import database.DatabaseOperator;
import models.Account;

public class AuthenticatorClass implements Authenticator {

    private final DatabaseOperator db;

    public AuthenticatorClass() {
        this.db = new DatabaseOperator();
    }

    @Override
    public void createAccount(String name, String pwd1, String pwd2) {
        try {
            // check if equal
            // hash password
            // ...
            db.createAccount(name, pwd1);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void deleteAccount(String username) {

    }

    @Override
    public Account getAccount(String username) {
        return null;
    }

    @Override
    public void changePwd(String name, String pwd1, String pwd2) {

    }

    @Override
    public Account login(String name, String pwd) {
        return null;
    }

    @Override
    public void logout(Account acc) {
    }
}