package resources;


import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
import database.exceptions.NotOwnerException;
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
import java.sql.SQLException;

@WebServlet("/followers")
public class ManageFollowerServlet extends HttpServlet {

    private static final Operation WRITE = Operation.WRITE ;
    private static final Operation PUT = Operation.PUT;


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
     * Submit follow request.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        int page = Integer.parseInt(request.getParameter("page"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.FOLLOWERS, WRITE, account);
            int ownerPage = accessController.getOwnerPage(account);
            accessController.submitFollowRequest(ownerPage, page, account);
            logger.authenticated("Created follow "+ ownerPage + " "+ page,  account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Follow Submitted");
            out.println("Owner page:"+ ownerPage);
            out.println("name:"+ account.getUsername());
            out.println("Page:"+ page);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            AuthenticationError.authenticationError(response);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            AccessControlError.accessControllerErrorOutput(response);
        } catch (NotOwnerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            AccessControlError.accessControllerErrorOutput(response);
        }
    }


    /**
     * Update follow status.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        //int ownerPage = Integer.parseInt(request.getParameter("ownerPage_update"));
        int page = Integer.parseInt(request.getParameter("page_update"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.FOLLOWERS, PUT, account);
            int ownerPage = accessController.getOwnerPage(account);
            accessController.updateFollowStatus(ownerPage, page, account);
            logger.authenticated("Updated follow "+ ownerPage + " "+ page,  account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Follow Updated");
            out.println("Owner page:"+ ownerPage);
            out.println("name:"+ account.getUsername());
            out.println("Page:"+ page);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            AuthenticationError.authenticationError(response);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }  catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            AccessControlError.accessControllerErrorOutput(response);
        } catch (NotOwnerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            AccessControlError.accessControllerErrorOutput(response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
