package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import database.exceptions.AccountNotFountException;
import database.exceptions.NameAlreadyExists;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    private Authenticator authenticator;
    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        super.init();
    }

    /**
     * Create a new account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd1 = request.getParameter("pwd1");
        String pwd2 = request.getParameter("pwd2");
        try {
            authenticator.createAccount(name, pwd1, pwd2);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (NameAlreadyExists e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Delete an account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doDelete(HttpServletRequest request,
                       HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd1 = request.getParameter("pwd1");
        String pwd2 = request.getParameter("pwd2");
        try {
            authenticator.deleteAccount(name);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Get an account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
        String name = request.getParameter("name");
        try {
            // send the account back to the client
            Account account = authenticator.getAccount(name);
            // send account back to client

            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}

