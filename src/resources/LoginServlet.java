package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManagerClass;
import authenticator.utils.JWTUtils;
import authorization.AccessController;
import authorization.AccessControllerClass;
import authorization.Capability;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.LockedAccountException;
import database.exceptions.UndefinedAccount;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String CAPABILITY = "capability";
    private Authenticator authenticator;
    private LogManagerClass logger;
    private AccessController accessController;
    @Override
    public void init() throws ServletException {
        this.authenticator = AuthenticatorClass.getAuthenticator();
        this.logger = new LogManagerClass();
        this.accessController = new AccessControllerClass();
        super.init();
    }
    @Override
    public void destroy() {
        super.destroy();
    }


    /**
     * Login user
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String pwd = request.getParameter("password");
        try {
            Account authUser = authenticator.authenticate_user(name, pwd);
            HttpSession session = request.getSession(true);
            String token = authUser.getJWT(session.getId());
            session.setAttribute(JWTUtils.JWT, token);
            setCapabilities(session, authUser);
            //compute token(s); send token(s) in next reply (cookie)
            //continue with authenticated user (redirect?)
            logger.authenticated("LOGIN", name,authUser.getUsername());
            response.sendRedirect("main_page.html");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException | UndefinedAccount e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            print(response, "Not Found");
        }
        catch (LockedAccountException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            print(response, "Forbidden Error");
        }
        catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            print(response, "Authentication Error");
        }
        catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        try {
            Account user = authenticator.check_authenticated_request(request, response);
            authenticator.logout(user);
            HttpSession session = request.getSession(false);
            if (session != null ) session.invalidate();
            logger.authenticated("LOG OUT", user.getUsername(),user.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            print(response, "Logged out");
        }
        catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            print(response, "Need to login first");
        }
    }
    private void print(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        out.println("<br/>");
        out.println("<a href="+ "index.html" +">Continue</a>");
        out.close();
    }

    private void setCapabilities(HttpSession session, Account user) {
        List<Capability> caps = accessController.createKey(user);
        for( Capability cap : caps){
            UUID uuid = UUID.randomUUID();
            session.setAttribute(CAPABILITY+ uuid,
                    JWTUtils.createJWTPermissions(user.getUsername(),session.getId(),cap));
        }

    }
}
