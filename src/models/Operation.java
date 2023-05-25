package models;

public enum Operation {
    READ("read"),
    WRITE("write"),
    DELETE("delete");

    private final String description;

    private Operation(String description) {
        this.description = description;

    }

    public String getDescription() {
        return this.description;
    }
}