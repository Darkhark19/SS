package database.exceptions;

public class LockedAccountException extends Exception{
    public LockedAccountException(){
        super("Account is locked.");
    }
}
