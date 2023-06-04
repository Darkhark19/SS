package authenticator;


import authenticator.utils.JWTUtils;
import authenticator.utils.PasswordUtils;
import database.DatabaseOperator;
import database.exceptions.*;
import models.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AuthenticatorClass implements Authenticator {

    public static String LOCAL_PATH = "D:\\Faculdade\\SS\\Tomcat\\webapps\\myApp";
    /**
     * Please change to your local path where the lib/, src/, web/, etc. are located
     * (directory where you cloned the GitHub repository)
     */


    private final DatabaseOperator db;

    private static Authenticator authenticator;

    private AuthenticatorClass() {
        this.db = new DatabaseOperator();
    }

    public static Authenticator getAuthenticator() {
        if(authenticator == null)
            authenticator = new AuthenticatorClass();
        return authenticator;
    }

    @Override
    public void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists, RuntimeException, PasswordNotMatchException {

        try {
            if (!pwd1.equals(pwd2)) {
                throw new PasswordNotMatchException();
            } else if (accountExists(name)) {
                throw new NameAlreadyExists();
            }
            db.createAccount(name, pwd1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAccount(String name) throws AccountNotFountException, RuntimeException, DeleteAccountException {
        try {
            Account account = db.getAccount(name);
            if (account == null) {
                throw new AccountNotFountException();
            } else if (account.isLoggedIn() || !account.isLocked()) {
                throw new DeleteAccountException();
            } else {
                db.deleteAccount(name);
            }
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
    public void changePwd(String name, String pwd1, String pwd2) throws AccountNotFountException {
        try {
            if (!accountExists(name)) {
                throw new AccountNotFountException();
            } else if (!pwd1.equals(pwd2)) {
                throw new RuntimeException("Passwords do not match");
            }else{
                db.changePwd(name, pwd1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account authenticate_user(String name, String pwd) throws AccountNotFountException, LockedAccountException, UndefinedAccount, AuthenticationError {
        try {
            Account account = db.getAccount(name);
            if (account == null) {
                throw new UndefinedAccount();
            } else if (account.isLocked()) {
                throw new LockedAccountException();
            } else if (!PasswordUtils.checkPassword(pwd,account.getPassword())) {
                int tries = db.getAccountTries(name);
                if(tries >= 3){
                    db.setLock(name, true);
                    account.setLocked(true);
                }
                db.updateAccountTries(name, db.getAccountTries(name) + 1);
                throw new AuthenticationError();
            } else {
                db.setLoggedIn(name, true);
                account.setLoggedIn(true);
                account.clearPassword();
                db.updateAccountTries(name, 0);
                return account;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public void logout(Account acc) throws AuthenticationError {
        try {
            if (!acc.isLoggedIn()) {
                throw new AuthenticationError();
            }
            acc.setLoggedIn(false);
            db.setLoggedIn(acc.getUsername(),false);
        } catch (SQLException | AccountNotFountException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationError {
        // check tokens in session against session info
        // refresh token (optional)
        // if not OK then raise AuthenticationError
        try {
            HttpSession session = request.getSession();
            Object token = session.getAttribute(JWTUtils.JWT);
            String id = session.getId();
            if (token == null)
                throw new AuthenticationError();
            String JWTToken = JWTUtils.parseJWT(token.toString(),id);
            if (JWTToken == null)
                throw new AuthenticationError();
            Account c = db.getAccount(JWTToken);
            c.getJWT(id);
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
