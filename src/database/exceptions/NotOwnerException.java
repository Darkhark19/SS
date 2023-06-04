package database.exceptions;

public class NotOwnerException extends Exception{
    public NotOwnerException() {
        super("Not the owner.");
    }
}
