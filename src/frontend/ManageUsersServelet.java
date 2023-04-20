package frontend;


import backend.Authenticator;
import backend.AuthenticatorClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ManageUsersServelet extends HttpServlet {


    static int counter = 0;
    AuthenticatorClass authenticatorClass = new AuthenticatorClass();

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<H1>Login</H1>");

            out.println(" <form name=“loginform” action=“myApp/Home” method=“POST”>");
            out.println(" <input type=\"text\" size=35 value=username/>");
            out.print("<input type=\"password\" size=35 value=password>");

            out.println("</form>");
        out.println("</BODY>");
        out.println("</HTML>");
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String pass = request.getParameter("password");
        System.out.println("username: " + username + " password: " + pass);

    }




}