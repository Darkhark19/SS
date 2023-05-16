package models;

public enum Roles {
    ADMIN("admin"),
    NORMAL("normal");

    private String description;

    private Roles(String description) {
        this.description = description;

    }

    public String getDescription() {
        return this.description;
    }

}
