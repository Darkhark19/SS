package authenticator;


import authenticator.utils.JWTUtils;
import authenticator.utils.PasswordUtils;
import database.DatabaseOperator;
import database.exceptions.*;
import models.Account;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Objects;

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
    public Account authenticate_user(String name, String pwd) throws AccountNotFountException, LockedAccountException, UndefinedAccount, AuthenticationError {
        try {
            Account account = db.getAccount(name);
            if (account == null) {
                throw new UndefinedAccount();
            } else if (account.isLocked()) {
                throw new LockedAccountException();
            } else if (!account.getPassword().equals(PasswordUtils.hashPassword(pwd))) {
                throw new AuthenticationError();
            } else {
                db.setLoggedIn(name, true);
                account.setLoggedIn(true);
                account.clearPassword();
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
    public Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationError {
        // check tokens in session against session info
        // refresh token (optional)
        // if not OK then raise AuthenticationError
        HttpSession session = request.getSession();
        String JWTToken = JWTUtils.parseJWT(session.getAttribute(JWTUtils.JWT).toString());
        System.out.println(JWTToken);
        String userCookie = request.getCookies()[0].getValue();
        System.out.println(userCookie);
        String userToken = JWTUtils.parseJWT(userCookie);
        System.out.println(userToken);
        if(Objects.equals(JWTToken, userToken)){
            Cookie cookie = new Cookie(JWTUtils.JWT, JWTUtils.createJWT(userToken));
            cookie.setMaxAge(10);
            response.addCookie(cookie);
        }
        throw new AuthenticationError();
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
