package database.exceptions;

public class UndefinedAccount extends Exception{

    public UndefinedAccount(){
        super("Account is undefined.");
    }
}
