package database.exceptions;

public class TimeExpiredTokenError extends Exception{
    public TimeExpiredTokenError(){
        super("Capability expired");
    }
}
