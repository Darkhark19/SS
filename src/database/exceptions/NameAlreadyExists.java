package database.exceptions;

public class NameAlreadyExists extends Exception{
    public NameAlreadyExists(){
        super("Name already exists.");
    }
}
