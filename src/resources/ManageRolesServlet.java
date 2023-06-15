package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import authorization.Capability;
import database.exceptions.AccessControlError;
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
import java.util.List;

@WebServlet("/roles")
public class ManageRolesServlet extends HttpServlet {

    private static final String SET = "SET";
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = request.getParameter("role");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            List<Capability> capabilities = accessController.getCapabilities(request, account.getUsername());
            accessController.checkPermission(capabilities, Resource.ROLES, Operation.CREATE, account);
            accessController.newRole(role);
            String name = account.getUsername();
            logger.authenticated(CREATE, name, name);
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Role Created");
            out.println("Role: " + role);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AccessControlError | AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("index.html");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String r = request.getParameter("give_role");
        String name = request.getParameter("name");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            List<Capability> capabilities = accessController.getCapabilities(request, account.getUsername());
            accessController.checkPermission(capabilities, Resource.ROLES, Operation.SET, account);
            Role role = accessController.getRole(r);
            accessController.setRole(account, role);
            logger.authenticated(SET, r, name);
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("User " + name + " has been given role " + r);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AccessControlError | AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("index.html");
        }
    }

}
