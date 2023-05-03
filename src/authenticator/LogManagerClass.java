package authenticator;

import java.io.FileWriter;
import java.io.IOException;

public class LogManagerClass implements LogManager{

    private FileWriter fileWriter;

    public LogManagerClass(){
        try {
            fileWriter = new FileWriter("logger.txt");
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    @Override
    public void authenticated(String operation, String name) {
        try {
            fileWriter.write("Operation: " + operation + " Name: " + name + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
