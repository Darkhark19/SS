package models;

public enum Resource {

    CHANGE_USERS("change_users"),
    USERS("users"),
    PERMISSIONS("permissions"),
    ROLES("roles"),
    LOGIN("login");

    private final String description;

    private Resource(String description) {
        this.description = description;

    }
    public String getDescription() {
        return this.description;
    }

    public static Resource getResource(String resource) {
        try{
            return Resource.valueOf(resource.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

    }

}
