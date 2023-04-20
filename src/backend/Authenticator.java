package backend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Authenticator implements AuthenticatorInterface{
    @Override
    public void create_account(String username, String password, String pwd2) {

    }

    @Override
    public void delete_account(String username) {

    }

    @Override
    public Account get_account(String username) {
        return null;
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) {

    }

    @Override
    public Account login(String name, String pwd) {
        return null;
    }

    @Override
    public void logout(Account acc) {

    }

    @Override
    public Account login(HttpServletRequest req, HttpServletResponse resp) {
        return null;
    }
}
