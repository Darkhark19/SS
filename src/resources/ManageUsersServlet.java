package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.NameAlreadyExists;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users")
public class ManageUsersServlet extends HttpServlet {

    private static final String GET = "GET";
    private static final String CREATE = "CREATE";
    private static final String CHANCE_PASSWORD = "CHANCE_PASSWORD";
    private Authenticator authenticator;
    private LogManager logger;


    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        //this.logger = new LogManagerClass();
        super.init();
    }

    /**
     * Create a new account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
        String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
        System.out.println("Create");
        System.out.println(name);
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            authenticator.createAccount(name, pwd1, pwd2);
            System.out.println("Log create");
            //logger.authenticated(CREATE, account.getUsername());
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.sendRedirect("main_page.html");
        } catch (NameAlreadyExists e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.sendRedirect("register.html");
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("register.html");
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("register.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get an account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        try {
            Account acc = authenticator.check_authenticated_request(request, response);
            // send the account back to the client
            Account account = authenticator.getAccount(name);
            // send account back to client
           // logger.authenticated(GET, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            PrintWriter pwriter=response.getWriter();
            pwriter.println("User Details Page:");
            pwriter.println("<br/>");
            pwriter.println("Username: "+account.getUsername());
            //pwriter.println("pwd: "+account.getPassword());
            pwriter.println("<br/>");
            pwriter.println("<a href='main_page.html'>Back</a>");
            pwriter.close();
        }
        catch (AccountNotFountException e) {
            PrintWriter pwriter= response.getWriter();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/html");
            pwriter.println("Username: "+name+" not found");
            //pwriter.println("pwd: "+account.getPassword());
            pwriter.println("<br/>");
            pwriter.println("<a href='main_page.html'>Back</a>");
            pwriter.close();
        } catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("get_page.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}

