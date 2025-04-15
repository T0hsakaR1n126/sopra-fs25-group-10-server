package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameGuessDTO {
    private Long gameId;
    private Long playerId;
    private String country;
    private int hintNumberUsed;
    public Long getGameId() {
        return gameId;
    }
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public int getHintNumberUsed() {
        return hintNumberUsed;
    }
    public void setHintNumberUsed(int hintNumberUsed) {
        this.hintNumberUsed = hintNumberUsed;
    }
}
