package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.LogManagerClass;
import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.DeleteAccountException;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/change_users")
public class ChangeUsersServlet extends HttpServlet {

    private static final String DELETE = "DELETE";
    private static final String CANNOT_DELETE = "Cannot delete this account";
    private static final String DELETE_PAGE = "'delete_page.html'";
    private static final String LOGIN_PAGE = "'index.html'";
    private static final String CHANCE_PASSWORD = "Password changed";
    private static final String CHANGE_PASSWORD_PAGE = "'change_pwd_page.html'";

    private static final String NOT_FOUND = "Account not found";

    private Authenticator authenticator;
    private LogManager logger;


    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        this.logger = new LogManagerClass();
        super.init();
    }
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            String operator = account.getUsername();
            String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
            String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
            authenticator.changePwd(name, pwd1, pwd2);
            logger.authenticated(CHANCE_PASSWORD,name, operator);
            response.setStatus(HttpServletResponse.SC_OK);
            print(response, "Password changed", "main_page.html");
        } catch ( AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.authenticated(CHANCE_PASSWORD + " Error",name, "Unknown");
            print(response,  "Need to login first", LOGIN_PAGE);
        } catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.authenticated(CHANCE_PASSWORD + " Error",name, "Unknown");
            print(response,  "Account not found", CHANGE_PASSWORD_PAGE);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        try {
            Account acc = authenticator.check_authenticated_request(request, response);
            authenticator.deleteAccount(name);
            logger.authenticated(DELETE,name, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            print(response, "Account Deleted", "main_page.html");
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.authenticated(DELETE+" "+NOT_FOUND, name, "Unknown");
            print(response, NOT_FOUND, DELETE_PAGE);
        }
        catch ( AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.authenticated(DELETE+" No authentication", name, "Unknown");
            print(response,  "Need to login first", LOGIN_PAGE);

        } catch (DeleteAccountException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.authenticated(DELETE+" Unauthorized", name, "Unknown");
            print(response, CANNOT_DELETE, DELETE_PAGE);
        } catch (RuntimeException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void print(HttpServletResponse response, String message, String url) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        out.println("<br/>");
        out.println("<a href="+url+">Continue</a>");
        out.close();
    }
}