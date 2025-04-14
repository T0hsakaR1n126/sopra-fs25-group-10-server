package ch.uzh.ifi.hase.soprafs24.constant;

public enum SessionStatus {
    PENDING("Waiting for players to join"),
    ACTIVE("Session active, game in progress"),
    COMPLETED("Game has ended"),
    CANCELLED("Session cancelled");

    private final String description;

    SessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
