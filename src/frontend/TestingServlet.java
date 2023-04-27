package frontend;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/private/Test")
public class TestingServlet extends HttpServlet {

    static int counter = 0;

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<H1>The Counter App!</H1>");
        out.println("<H1>Value=" + counter + "</H1>");
        out.print("<form action=\"");
        out.print("Test\" ");
        out.println("method=GET>");
        out.println("<br>");
        out.println("<input type=submit name=increment>");
        out.println("</form>");
        out.println("</BODY>");

        out.println("</HTML>");
        counter++;
    }
}

