package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerSimpleDTO {
    private Long playerId;
    private String token;

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
