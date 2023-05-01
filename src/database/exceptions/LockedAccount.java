package database.exceptions;

public class LockedAccount extends Exception{
    public LockedAccount(){
        super("Account is locked.");
    }
}
