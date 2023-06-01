package models;

public enum Operation {
    READ("READ"),
    SET("SET"),
    WRITE("WRITE"),
    GRANT("GRANT"),
    CREATE("CREATE"),
    REVOKE("REVOKE"),
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