package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;
import authenticator.utils.PasswordUtils;
import database.exceptions.AccountNotFountException;
import database.exceptions.AuthenticationError;
import database.exceptions.LockedAccount;
import database.exceptions.UndefinedAccount;
import models.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginServelet extends HttpServlet {

    private Authenticator authenticator;
    private static final String USER ="user";
    private static final String PWD ="pwd";
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

    }

    /**
     * Delete an account.
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd1 = request.getParameter("pwd1");
        String pwd2 = request.getParameter("pwd2");
        try {
            authenticator.deleteAccount(name);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (AccountNotFountException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Get credencials to login
     * @param request The request object.
     * @param response The response object.
     */
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
        String name = request.getParameter("name");
        String pwd1 = PasswordUtils.hashPassword(request.getParameter("password"));
        try {
            Account authUser = authenticator.login(name, pwd1);
            HttpSession session = request.getSession(true);
            pwd1 = null;
            session.setAttribute(JWT, authUser.getJWT());
            //compute token(s); send token(s) in next reply (cookie)
            //continue with authenticated user (redirect?)
            Cookie cookie = new Cookie(JWT, name);
            cookie.setMaxAge(10);
            response.addCookie(cookie);
            response.sendRedirect("/mainPage.html");
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UndefinedAccount e) {
            throw new RuntimeException(e);
        } catch (AuthenticationError e) {
            // handle authentication error
            System.out.println(e.getMessage());
        } catch (LockedAccount e) {
            System.out.println(e.getMessage());
        } catch (AccountNotFountException e) {
            System.out.println(e.getMessage());
        }
    }
}
