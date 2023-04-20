package backend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

    public interface Authenticator {

    void create_account(String username, String password, String pwd2);

    void delete_account(String username);

    Account get_account(String username);

    void change_pwd(String name, String pwd1, String pwd2);

    Account login(String name, String pwd);

    void logout(Account acc);

    Account login(HttpServletRequest req, HttpServletResponse resp);
}
