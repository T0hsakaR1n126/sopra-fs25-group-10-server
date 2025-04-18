package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TeamLeaderboardEntryDTO {
    private Long gameId;
    private String gameName;
    private LocalDateTime gameStartTime;

    private Long teamId;
    private String teamName;
    private int teamScore;
    private List<String> teamPlayers;
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
    public Long getTeamId() {
        return teamId;
    }
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public int getTeamScore() {
        return teamScore;
    }
    public void setTeamScore(int teamScore) {
        this.teamScore = teamScore;
    }
    public List<String> getTeamPlayers() {
        return teamPlayers;
    }
    public void setTeamPlayers(List<String> teamPlayers) {
        this.teamPlayers = teamPlayers;
    }
    

}