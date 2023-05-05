package database.exceptions;

public class DeleteAccountException extends Exception{
    public DeleteAccountException() {
        super("Cannot delete account");
    }
}
