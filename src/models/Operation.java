package models;

public enum Operation {
    READ("READ"),
    WRITE("WRITE"),
    PUT("PUT"),
    LIKE("LIKE"),
    UNLIKE("UNLIKE"),
    DELETE("DELETE");

    private final String description;

    private Operation(String description) {
        this.description = description;

    }

    public String getDescription() {
        return this.description;
    }

    public static Operation getOperation(String opId) {
        try{
            return Operation.valueOf(opId.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

    }
}