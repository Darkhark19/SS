package database.exceptions;

public class NoPageFound extends Exception {
    public NoPageFound() {
        super("You don't have a page.");
    }
}
