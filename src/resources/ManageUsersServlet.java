package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authenticator.utils.PasswordUtils;
import authorization.AccessController;
import authorization.AccessControllerClass;
import authorization.Capability;
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
import java.util.List;

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
            List<Capability> capabilities = accessController.getCapabilities(request, account.getUsername());
            accessController.checkPermission(capabilities, Resource.USERS, Operation.WRITE, account);
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
            response.sendRedirect("register.html");
        } catch (PasswordNotMatchException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("register.html");
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.sendRedirect("register.html");
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }


    /**
     * Get an account.
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
            List<Capability> capabilities = accessController.getCapabilities(request, acc.getUsername());
            accessController.checkPermission(capabilities, Resource.USERS, Operation.READ, acc);
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
            response.sendRedirect("main_page.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}

