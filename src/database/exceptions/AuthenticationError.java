package database.exceptions;

public class AuthenticationError extends Exception{
    public AuthenticationError(){
        super("Authentication error.");
    }
}
