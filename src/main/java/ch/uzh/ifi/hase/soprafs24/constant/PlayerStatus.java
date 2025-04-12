package ch.uzh.ifi.hase.soprafs24.constant;

public enum PlayerStatus {
    WAITING("Waiting to join the lobby"),
    SEARCHING("Searching for opponent"),
    IN_GAME("In Game"),
    FINISHED("Player has completed the game"),
    DISCONNECTED("Player has disconnected");

    private final String status;

    PlayerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
