package models;

public enum Resource {

    CHANGE_USERS("change_users"),
    USERS("users"),
    LOGIN("login");

    private final String description;

    private Resource(String description) {
        this.description = description;

    }
    public String getDescription() {
        return this.description;
    }
}
