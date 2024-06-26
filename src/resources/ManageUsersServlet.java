package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authenticator.utils.PasswordUtils;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.*;
import models.Account;
import models.Operation;
import models.Resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users")
public class ManageUsersServlet extends HttpServlet {

    private static final String GET = "GET";
    private static final String CREATE = "CREATE";
    private Authenticator authenticator;
    private LogManager logger;

    private AccessController accessController;

    @Override
    public void init() throws ServletException {
        this.authenticator = AuthenticatorClass.getAuthenticator();
        accessController = new AccessControllerClass();
        this.logger = new LogManagerClass();
        super.init();
    }

    /**
     * Create a new account.
     *
     * @param request  The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
        String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.USERS, Operation.WRITE, account);
            authenticator.createAccount(name, pwd1, pwd2);
            logger.authenticated(CREATE, name, account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Account Created");
            out.println("Username:" + name);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (NameAlreadyExists e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            print(response, "Username already exists");
        } catch (PasswordNotMatchException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            print(response, "Passwords don't match");
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            print(response, "Something went wrong");
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            print(response, "Not enough permissions");
        }
    }


    /**
     * Get an account. Nao usado
     *
     * @param request  The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        try {
            Account acc = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.USERS, Operation.READ, acc);
            // send the account back to the client
            Account account = authenticator.getAccount(name);
            // send account back to client
            logger.authenticated(GET, name, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            PrintWriter pwriter = response.getWriter();
            pwriter.println("User Details Page:");
            pwriter.println("<br/>");
            pwriter.println("Username: " + account.getUsername());
            pwriter.println("<br/>");
            pwriter.println("<a href='main_page.html'>Back</a>");
            pwriter.close();
        } catch (AccountNotFountException e) {
            PrintWriter pwriter = response.getWriter();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.authenticated(GET + " Error", name, "Unknown");
            response.setContentType("text/html");
            pwriter.println("Username: " + name + " not found");
            pwriter.println("<br/>");
            pwriter.println("<a href='main_page.html'>Back</a>");
            pwriter.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.authenticated(GET + " Error", name, "Unknown");
            AuthenticationError.authenticationError(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.authenticated("GET AccessControlError", name, "Unknown");
            AccessControlError.accessControllerErrorOutput(response);
        }
    }

    private void print(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        out.println("<br/>");
        out.println("<a href=" + "register.html" + ">Continue</a>");
        out.close();
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}

