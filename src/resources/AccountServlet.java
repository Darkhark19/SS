package resources;

import authenticator.Authenticator;
import authenticator.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    private Authenticator authenticator;
    @Override
    public void init() throws ServletException {
        this.authenticator = new AuthenticatorClass();
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) {
        // TODO: finish logic and result mapping
        String name = request.getParameter("name");
        String pwd1 = request.getParameter("pwd1");
        String pwd2 = request.getParameter("pwd2");
        authenticator.createAccount(name, pwd1, pwd2);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}

