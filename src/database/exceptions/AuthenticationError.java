package database.exceptions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthenticationError extends Exception{
    public AuthenticationError(){
        super("Authentication error.");
    }

    public static void authenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("Authentication error");
        out.println("<a href='index.html'>Back</a>");
        out.close();
    }
}
