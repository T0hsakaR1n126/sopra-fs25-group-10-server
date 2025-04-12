package ch.uzh.ifi.hase.soprafs24.constant;

public enum GameAllow {
    PUBLIC("Public"),
    PRIVATE("Private");

    private final String description;

    GameAllow(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}