package database.exceptions;

public class PasswordNotMatchException extends Exception {
    public PasswordNotMatchException() {
        super("Passwords do not match");
    }
}
