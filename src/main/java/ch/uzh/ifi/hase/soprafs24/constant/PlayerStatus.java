package ch.uzh.ifi.hase.soprafs24.constant;

public enum PlayerStatus {
    OFFLINE("Offline"),
    ONLINE("Online"),
    SEARCHING("Searching for opponent"),
    IN_GAME("In Game"),
    IDLE("Idle");

    private final String status;

    PlayerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
