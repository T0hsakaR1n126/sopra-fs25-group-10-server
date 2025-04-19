package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;

public class OneVsOneLeaderboardEntryDTO {
    private Long gameId;
    private String gameName;
    private LocalDateTime gameStartTime;

    private Long playerId;
    private String playerName;
    private String playerScore;

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public String getPlayerScore() {
        return playerScore;
    }
    public void setPlayerScore(String playerScore) {
        this.playerScore = playerScore;
    }
    public Long getGameId() {
        return gameId;
    }
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public LocalDateTime getGameStartTime() {
        return gameStartTime;
    }
    public void setGameStartTime(LocalDateTime gameStartTime) {
        this.gameStartTime = gameStartTime;
    }
    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

}

