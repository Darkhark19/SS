package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.NameAlreadyExists;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users")
public class ManageUsersServlet extends HttpServlet {

    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private static final String CREATE = "CREATE";
    private static final String CHANCE_PASSWORD = "CHANCE_PASSWORD";
    private Authenticator authenticator;
    private LogManager logger;


    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        this.logger = new LogManagerClass();
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
        String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
        String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            authenticator.createAccount(name, pwd1, pwd2);
            logger.authenticated(CREATE, account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (NameAlreadyExists e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        try {
            Account acc = authenticator.check_authenticated_request(request, response);
            authenticator.deleteAccount(name);
            logger.authenticated(DELETE, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (RuntimeException | AuthenticationError e) {
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
            Account acc = authenticator.check_authenticated_request(request, response);
            // send the account back to the client
            Account account = authenticator.getAccount(name);
            // send account back to client
            logger.authenticated(GET, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            PrintWriter pwriter=response.getWriter();
            pwriter.println("User Details Page:");
            pwriter.println("Username: "+account.getUsername());
            pwriter.println("pwd: "+account.getPassword());
            pwriter.close();
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doPut(HttpServletRequest request,
                      HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
        String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
        try {
            authenticator.check_authenticated_request(request, response);
            authenticator.changePwd(name, pwd1, pwd2);
            logger.authenticated(CHANCE_PASSWORD, name);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RuntimeException | AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}

