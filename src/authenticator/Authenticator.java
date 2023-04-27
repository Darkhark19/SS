package authenticator;

import database.exceptions.AccountNotFountException;
import database.exceptions.NameAlreadyExists;
import models.Account;

public interface Authenticator {

    void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists;

    void deleteAccount(String name) throws AccountNotFountException;

    Account getAccount(String name) throws AccountNotFountException;

    void changePwd(String name, String pwd1, String pwd2);

    Account login(String name, String pwd);

    void logout(Account acc);
}
