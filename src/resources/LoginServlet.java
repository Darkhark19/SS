package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.LockedAccountException;
import database.exceptions.UndefinedAccount;
import models.Account;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private Authenticator authenticator;
    private static final String JWT ="jwt";
    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        super.init();
    }
    @Override
    public void destroy() {
        super.destroy();
    }


    /**
     * Login user
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd = request.getParameter("password");
        try {
            Account authUser = authenticator.authenticate_user(name, pwd);
            HttpSession session = request.getSession(true);
            session.setAttribute(JWT, authUser.getJWT());
            //compute token(s); send token(s) in next reply (cookie)
            //continue with authenticated user (redirect?)
            Cookie cookie = new Cookie(JWT, name);
            cookie.setMaxAge(10);
            response.addCookie(cookie);
            response.sendRedirect("mainPage.html");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException | UndefinedAccount e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (LockedAccountException | AccountLockedException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        catch (AuthenticationError e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
