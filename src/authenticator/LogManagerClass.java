package authenticator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.APPEND;

public class LogManagerClass implements LogManager {

    private File file;

    private static final String TOMCAT_PATH = "D:\\Faculdade\\SS\\Tomcat\\webapps";
    private static final String PATH ="myApp"+File.separator +"src" + File.separator + "authenticator"+ File.separator+"logger.txt";
    public LogManagerClass() {
        String absoluteFilePath = TOMCAT_PATH + File.separator + PATH;
        file = new File(absoluteFilePath);
        try {
            if(file.createNewFile()) {
                System.out.println("Successfully create the file.");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void authenticated(String operation, String name) {
        try {
            String response = "Operation: " + operation + " Name: " + name + "\n";
            Files.write(file.toPath(),response.getBytes(),APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


   public static void main(String[] args) {
        LogManagerClass logManagerClass = new LogManagerClass();
        logManagerClass.authenticated("CREATE", "teste");
        logManagerClass.authenticated("CREATE", "te12312ste");
    }
}
