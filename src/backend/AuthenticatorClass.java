package backend;

import database.DatabaseConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;

public class AuthenticatorClass implements Authenticator{

    private Connection connection;
    public AuthenticatorClass() {
        this.connection = DatabaseConnection.getConnection();
    }
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
