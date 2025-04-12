package ch.uzh.ifi.hase.soprafs24.websocket.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;

public class MatchRequestDTO {

    private String playerId; // The ID of the player making the request
    private String teamId;   // The ID of the team (if relevant, for Team vs Team)
    private GameMode mode;   // The game mode the player wants to play (e.g., 1v1 or Team vs Team)

    // Getters and Setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }
}
