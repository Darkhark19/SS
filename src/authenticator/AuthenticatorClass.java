package authenticator;

import authenticator.utils.JWTUtils;
import authenticator.utils.PasswordUtils;
import database.DatabaseOperator;
import database.exceptions.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.Account;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.sql.SQLException;
import java.util.Date;

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
        try {
            if (accountExists(name)) {
                throw new NameAlreadyExists();
            }
            db.createAccount(name, pwd1);
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
            return account.clone();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void changePwd(String name, String pwd1, String pwd2) {

    }

    @Override
    public Account login(String name, String pwd)
            throws UndefinedAccount, LockedAccount,
            AuthenticationError, AccountNotFountException {
        try {
            Account account = db.getAccount(name);
            if (account == null) {
                throw new UndefinedAccount();
            } else if (account.isLocked()) {
                throw new LockedAccount();
            } else if (!account.getPassword().equals(pwd)) {
                throw new AuthenticationError();
            } else {
                account.setLoggedIn(true);
                db.loginAccount(name);
                return account;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public void logout(Account acc) {
        try {
            acc.setLoggedIn(false);
            db.logoutAccount(acc.getUsername());
        } catch (SQLException | AccountNotFountException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account login(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public String createToken(Account acc) {
        JWTUtils.createJWT(acc.getUsername(), "authenticator", "authenticator", 100000);
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
