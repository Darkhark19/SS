package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.LogManager;
import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
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
    private Authenticator authenticator;
    private LogManager logger;


    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        //this.logger = new LogManagerClass();
        super.init();
    }
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        try {
            Account account = authenticator.check_authenticated_request(request, response);
            String name = account.getUsername();
            String pwd1 = PasswordUtils.hashPassword(request.getParameter("pwd1"));
            String pwd2 = PasswordUtils.hashPassword(request.getParameter("pwd2"));
            authenticator.changePwd(name, pwd1, pwd2);
            //logger.authenticated(CHANCE_PASSWORD, name);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RuntimeException | AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
        String name = request.getParameter("name");
        try {
            System.out.println(DELETE);
            Account acc = authenticator.check_authenticated_request(request, response);
            authenticator.deleteAccount(name);
            PrintWriter out = response.getWriter();
            out.println("Account deleted");
            // logger.authenticated(DELETE, acc.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
        catch (RuntimeException | AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
