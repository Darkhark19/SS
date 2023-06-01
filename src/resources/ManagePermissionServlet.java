package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.AuthenticationError;
import models.Account;
import models.Operation;
import models.Resource;
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

    private static final String GRANT = "GRANT";
    private static final String REVOKE = "REVOKE";
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
        String resource = request.getParameter("resource");
        String operation = request.getParameter("operation");
        try{
            Account account = authenticator.check_authenticated_request(request, response);
            Role r = accessController.getRole(role);
            //accessController.checkPermission();
            accessController.grantPermission(r, Resource.getResource(resource), Operation.getOperation(operation));
            logger.authenticated(GRANT, role , account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Permission Created");
            out.println("Role: "+role);
            out.println("Resource: "+resource);
            out.println("Operation: "+operation);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("index.html");
        } /*catch (AccessControlError e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("main_page.html");
        }*/
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String r = request.getParameter("give_role");
        String name = request.getParameter("name");
        try{
            Account account = authenticator.check_authenticated_request(request, response);
            Role role = accessController.getRole(r);
            accessController.setRole(account,role);
            logger.authenticated(REVOKE, r, name);
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
