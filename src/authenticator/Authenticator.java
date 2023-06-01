package authenticator;

import database.exceptions.*;
import models.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator {

    void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists, PasswordNotMatchException;

    void deleteAccount(String name) throws AccountNotFountException, DeleteAccountException;

    Account getAccount(String name) throws AccountNotFountException;

    void changePwd(String name, String pwd1, String pwd2) throws AccountNotFountException;

    Account authenticate_user(String name, String pwd) throws AccountNotFountException, LockedAccountException, UndefinedAccount, AuthenticationError;
    void logout(Account acc) throws AuthenticationError;

    Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationError;



}
