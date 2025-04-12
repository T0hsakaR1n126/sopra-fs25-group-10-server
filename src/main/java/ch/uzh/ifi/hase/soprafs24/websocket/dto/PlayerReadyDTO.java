package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class PlayerReadyDTO {
    private Long playerId;
    private Long gameId;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
