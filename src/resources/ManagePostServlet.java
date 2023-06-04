package resources;


import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.SN;
import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
import database.exceptions.NotOwnerException;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/manage_post")
public class ManagePostServlet extends HttpServlet {

    private static final Operation DELETE = Operation.DELETE;
    private static final Operation WRITE = Operation.WRITE;


    private Authenticator authenticator;
    private LogManager logger;
    private SN app;

    private AccessController accessController;

    @Override
    public void init() throws ServletException {
        this.authenticator = AuthenticatorClass.getAuthenticator();
        accessController = new AccessControllerClass();
        this.logger = new LogManagerClass();
        this.app = SN.getInstance();
        super.init();
    }

    /**
     * Create a new post for page.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        int pageId = Integer.parseInt(request.getParameter("pageId"));
        String date = request.getParameter("date");
        String text = request.getParameter("text");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.MANAGE_POSTS, WRITE, account);
            accessController.checkPage(pageId, account);
            PostObject post = app.newPost(pageId,date,text);
            logger.authenticated("Created post ",  account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post Created");
            out.println("post: "+ post.getPostId());
            out.println("name: "+ account.getUsername());
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
        } catch (AccessControlError | NotOwnerException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }


    /**
     * Delete post of user
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request,Resource.PAGES , DELETE, account);
            PostObject p = accessController.checkPost(postId, account);
            app.deletePost( p);
            logger.authenticated("Delete post: "+ p.getPostId(),account.getUsername() , account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post Deleted");
            out.println("Post:"+ p.getPostId());
            out.println("Post text:"+ p.getPostText());
            out.println("name:"+ account.getUsername());
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
        } catch (AccessControlError | NotOwnerException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
