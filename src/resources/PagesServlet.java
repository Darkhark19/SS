package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authorization.AccessController;
import authorization.AccessControllerClass;
import authorization.Capability;
import database.SN;
import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
import database.exceptions.NotOwnerException;
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
import java.util.List;

@WebServlet("/pages")
public class PagesServlet extends HttpServlet {

    private static final Operation UPDATE = Operation.PUT;
    private static final Operation GET = Operation.READ;

    private Authenticator authenticator;
    private LogManager logger;

    private AccessController accessController;
    private SN app;

    @Override
    public void init() throws ServletException {
        this.authenticator = AuthenticatorClass.getAuthenticator();
        accessController = new AccessControllerClass();
        this.logger = new LogManagerClass();
        app = SN.getInstance();
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        int pageId = Integer.parseInt(request.getParameter("pageId"));
        String user = request.getParameter("user");
        String pageTitle = request.getParameter("pageTitle");
        String pagePic = request.getParameter("pagePic");
        String email = request.getParameter("email");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            List<Capability> capabilities = accessController.getCapabilities(request, account.getUsername());
            accessController.checkPermission(capabilities, Resource.PAGES, UPDATE, account);
            accessController.updatePage(pageId, pageTitle, pagePic, email, user, account);
            logger.authenticated("Update page: " + pageId, user, account.getUsername());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("Page id:" + pageId);
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
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            List<Capability> capabilities = accessController.getCapabilities(request, account.getUsername());
            accessController.checkPermission(capabilities, Resource.PAGES, GET, account);
            List<PageObject> pages = app.getAllPages();
            logger.authenticated("Get pages ", account.getUsername(), account.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            for (PageObject page : pages) {
                out.println("Page id:" + page.getPageId());
                out.println("Page Title:" + page.getPageTitle());
                out.println("name:" + page.getUserId());
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
        } catch (AccessControlError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("main_page.html");
        }
    }
}
