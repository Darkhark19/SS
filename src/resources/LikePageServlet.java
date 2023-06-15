package resources;


import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
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
     * @param request The request object.
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
            logger.authenticated("Like post "+ postId,  account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post like:"+ postId);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (IOException | SQLException  e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        } catch ( PageNotFollowed e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("main_page.html");
        }
    }


    /**
     * Post unlike .
     * @param request The request object.
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
            logger.authenticated("Unike post "+ postId,  account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post unlike:"+ postId);
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        } catch (PageNotFollowed e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("main_page.html");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
