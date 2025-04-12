package ch.uzh.ifi.hase.soprafs24.constant;

public enum TeamStatus {
    READY("all players are ready"),
    SEARCHING("searching for opposing team"),
    INGAME("game in progress"),
    FINISHED("game finished"),
    ABORTED("game aborted"),
    WAITING("waiting for players"),
    DISBANDED("Team has been disbanded");
    private final String status;
    
    TeamStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
}
