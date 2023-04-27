package database.exceptions;

public class AccountNotFountException extends Exception{
    public AccountNotFountException(){
        super("Account not found.");
    }
}
