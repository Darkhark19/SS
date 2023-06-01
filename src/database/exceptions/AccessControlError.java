package database.exceptions;

public class AccessControlError extends Exception{

    public AccessControlError(){
        super("Permissions error.");
    }
}
