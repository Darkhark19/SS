package database.exceptions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AccessControlError extends Exception{

    public AccessControlError(){
        super("Permissions error.");
    }

    public static void accessControllerErrorOutput(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("Access denied");
        out.println("<a href='main_page.html'>Back</a>");
        out.close();
    }
}

