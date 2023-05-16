package authenticator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static authenticator.AuthenticatorClass.LOCAL_PATH;
import static java.nio.file.StandardOpenOption.APPEND;

public class LogManagerClass implements LogManager {

    private File file;
    private static final String PATH =  "src" + File.separator + "authenticator"+ File.separator+"logger.txt";
    public LogManagerClass() {
        String absoluteFilePath = LOCAL_PATH + File.separator + PATH;
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
    public void authenticated(String operation, String account, String operator) {
        try {
            Date date = new Date(System.currentTimeMillis());
            String response = date +" Operation: " + operation +
                    " Account: " + account +
                    " Done By Name: " + operator + "\n";
            Files.write(file.toPath(),response.getBytes(),APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
