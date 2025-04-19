package ch.uzh.ifi.hase.soprafs24.constant;

public enum GameAccessType {
    PUBLIC("Public"),
    PRIVATE("Private");

    private final String description;

    GameAccessType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}