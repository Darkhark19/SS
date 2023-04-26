package authenticator;

import models.Account;

public interface Authenticator {

    void createAccount(String name, String pwd1, String pwd2);

    void deleteAccount(String name);

    Account getAccount(String name);

    void changePwd(String name, String pwd1, String pwd2);

    Account login(String name, String pwd);

    void logout(Account acc);
}
