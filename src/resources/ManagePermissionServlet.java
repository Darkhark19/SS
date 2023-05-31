package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.AuthenticationError;
import models.Account;
import models.Role;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/permissions")
public class ManagePermissionServlet extends HttpServlet {

    private static final String SET = "GRANT";
    private static final String DELETE = "REVOKE";
    private Authenticator authenticator;
    private LogManager logger;

    private AccessController accessController;

    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        accessController = new AccessControllerClass();
        this.logger = new LogManagerClass();
        super.init();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = request.getParameter("role");
        try{
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.newRole(role);
            String name = account.getUsername();
            logger.authenticated(CREATE, name, name);
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Role Created");
            out.println("Role: "+role);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("index.html");
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String r = request.getParameter("give_role");
        String name = request.getParameter("name");
        try{
            Account account = authenticator.check_authenticated_request(request, response);
            Role role = accessController.getRole(r);
            accessController.setRole(account,role);
            logger.authenticated(SET, r, name);
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("User "+name+" has been given role "+r);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("index.html");
        }
    }
}
