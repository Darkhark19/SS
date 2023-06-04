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
import database.exceptions.PageNotFollowed;
import models.Account;
import models.Operation;
import models.PostObject;
import models.Resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/posts")
public class PostsServlet extends HttpServlet {

    private static final Operation UPDATE = Operation.PUT;
    private static final Operation GET = Operation.READ;
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

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        String post_text = request.getParameter("post_text");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.POSTS, UPDATE, account);
            accessController.updatePost(postId, post_text, account);
            logger.authenticated("Update post: " + postId, account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Post id:" + postId);
            out.println("Post text:" + post_text);
            out.println("name:" + account.getUsername());
            out.println("<br/>");
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError | NotOwnerException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        int pageId = Integer.parseInt(request.getParameter("pageId"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request, Resource.POSTS, GET, account);
            List<PostObject> p = accessController.checkPagePosts(pageId, account);
            logger.authenticated("Get posts ", account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Page:" + pageId);
            for (PostObject post : p) {
                out.println("Post id:" + post.getPostId());
                out.println("Post text:" + post.getPostText());
                out.println("name:" + account.getUsername());
                out.println("<br/>");
            }
            out.println("<a href='main_page.html'>Back</a>");
            out.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("index.html");
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AccessControlError | PageNotFollowed e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }

}
