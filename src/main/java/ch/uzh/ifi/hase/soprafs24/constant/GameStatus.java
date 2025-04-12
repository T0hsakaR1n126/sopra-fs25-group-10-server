package ch.uzh.ifi.hase.soprafs24.constant;

public enum GameStatus {
    WAITING("Waiting for players"),
    ACTIVE("Game in progress"),
    FINISHED("Game finished"),
    CANCELLED("Game cancelled");

    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
