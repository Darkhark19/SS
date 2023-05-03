package authenticator;

import database.exceptions.*;
import models.Account;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator {

    void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists;

    void deleteAccount(String name) throws AccountNotFountException;

    Account getAccount(String name) throws AccountNotFountException;

    void changePwd(String name, String pwd1, String pwd2);

    Account authenticate_user(String name, String pwd) throws AccountNotFountException, AccountLockedException, LockedAccountException, UndefinedAccount, AuthenticationError;
    void logout(Account acc);

    Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationError;

}
