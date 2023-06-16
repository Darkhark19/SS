package resources;


import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
import database.exceptions.NoPageFound;
import database.exceptions.PageNotFollowed;
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

@WebServlet("/likes")
public class LikePageServlet extends HttpServlet {

    private static final Operation LIKE = Operation.LIKE;
    private static final Operation UNLIKE = Operation.UNLIKE;
    private AccessController accessController;
    private Authenticator authenticator;
    private LogManager logger;

    @Override
    public void init() throws ServletException {
        this.authenticator = AuthenticatorClass.getAuthenticator();
        accessController = new AccessControllerClass();
        this.logger = new LogManagerClass();
        super.init();
    }

    /**
     * Post like.
     *
     * @param request  The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.LIKES, LIKE, account);
            accessController.likePost(postId, account);
            logger.authenticated("Like post " + postId, account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post like:" + postId);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            AuthenticationError.authenticationError(response);
        } catch (IOException | SQLException | RuntimeException e) {
            print(response, "Something went wrong.");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NoPageFound e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            print(response, "You need to have a page first.");
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            AccessControlError.accessControllerErrorOutput(response);
        } catch (PageNotFollowed e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            AccessControlError.accessControllerErrorOutput(response);
        }
    }


    /**
     * Post unlike .
     *
     * @param request  The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        int postId = Integer.parseInt(request.getParameter("postId_unlike"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.LIKES, UNLIKE, account);
            accessController.unlikePost(postId, account);
            logger.authenticated("Unlike post " + postId, account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post unlike:" + postId);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            AuthenticationError.authenticationError(response);
        } catch (IOException | SQLException | RuntimeException e) {
            print(response, "Something went wrong. Please try again.");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            AccessControlError.accessControllerErrorOutput(response);
        } catch (PageNotFollowed e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            AccessControlError.accessControllerErrorOutput(response);
        }
    }

    private void print(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        out.println("<br/>");
        out.println("<a href=" + "like_page.html" + ">Continue</a>");
        out.close();
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
