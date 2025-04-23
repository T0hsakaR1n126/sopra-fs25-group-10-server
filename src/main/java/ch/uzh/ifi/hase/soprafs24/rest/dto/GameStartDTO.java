package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class GameStartDTO {
    private Long playerId;
    private String token;

    // Getters and setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
