package resources;


import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import database.SN;
import database.exceptions.*;
import models.Account;
import models.Operation;
import models.PageObject;
import models.Resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/manage_page")
public class ManagePageServlet extends HttpServlet {

    private static final Operation DELETE = Operation.DELETE;
    private static final Operation CREATE = Operation.WRITE;


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
     * Create a new Page for user.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String page_title = request.getParameter("page_title");
        String page_pic = request.getParameter("page_pic");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request,Resource.PAGES , CREATE, account);
            app.newPage(name,email,page_title,page_pic);
            logger.authenticated("Created page", name, account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("PAGE Created");
            out.println("Page:"+ page_title);
            out.println("name:"+ name);
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
        }
    }


    /**
     * Delete page of user
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        int page_id = Integer.parseInt(request.getParameter("page_id"));
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            accessController.checkPermission(request,Resource.PAGES , DELETE, account);
            PageObject page = app.getPage(page_id);
            app.deletePage( page);
            logger.authenticated("Delete page", page.getUserId(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Page Deleted");
            out.println("Page:"+ page.getPageTitle());
            out.println("name:"+ page.getUserId());
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
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
