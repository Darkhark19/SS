package authenticator;

import authenticator.utils.PasswordUtils;
import database.DatabaseOperator;
import database.exceptions.AccountNotFountException;
import database.exceptions.NameAlreadyExists;
import models.Account;

import java.sql.SQLException;

public class AuthenticatorClass implements Authenticator {

    private final DatabaseOperator db;

    public AuthenticatorClass() {
        this.db = new DatabaseOperator();
    }

    @Override
    public void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists, RuntimeException {
        if (!pwd1.equals(pwd2)) {
            throw new RuntimeException("Passwords do not match");
        }
        String hashedPwd = PasswordUtils.hashPassword(pwd1);
        try {
            if (accountExists(name)) {
                throw new NameAlreadyExists();
            }
            db.createAccount(name, hashedPwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAccount(String name) throws AccountNotFountException, RuntimeException {
        try {
            Account account = db.getAccount(name);
            if (account == null)
                throw new AccountNotFountException();
            if (account.isLoggedIn() || !account.isLocked())
                throw new RuntimeException("Cannot delete account");
            db.deleteAccount(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account getAccount(String username) throws AccountNotFountException {
        try {
            Account account = db.getAccount(username);
            if (account == null)
                throw new AccountNotFountException();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    // check if account with given name exists
    public boolean accountExists(String name) {
        try {
            Account account = db.getAccount(name);
            if (account != null) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
