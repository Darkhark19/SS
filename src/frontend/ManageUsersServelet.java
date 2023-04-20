package frontend;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ManageUsersServelet extends HttpServlet {


    static int counter = 0;

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

            out.println(" <form name=“loginform” action=“http://www.mydomain.com/login” method=“POST”>");
            out.println(" <input type=“text\" size=35 value=username'/>");
            out.print("<form action=\"");
            out.print("<input type=\"password\" size=35 value='password'>");
            out.println(" <input type=\"hidden\" value=redirect_url>");
            out.println("</form>");
        out.println("</BODY>");
        out.println("</HTML>");
        counter ++;
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        performTask(request, response);
    }

    private void performTask(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("TestJndiServlet says hi");
        out.println("<br/>");

    }


}