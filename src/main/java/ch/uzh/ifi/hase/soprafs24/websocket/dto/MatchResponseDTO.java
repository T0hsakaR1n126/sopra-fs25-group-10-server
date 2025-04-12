package ch.uzh.ifi.hase.soprafs24.websocket.dto;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;


public class MatchResponseDTO {
    private Long gameId;
    private String playerId;  // List of player Id involved in the game
    private String opponentPlayerId;  // List of opponent player Id for 1v1 and team games
    private String teamId;    // List of team Id involved in the game (for Team vs Team mode)
    private String opponentTeamId;  // List of opponent team Id for Team vs Team
    private GameMode mode;         // Game mode (1v1 or Team vs Team)
    private GameStatus gameStatus;     // Status of the game (optional)

    // Getters and Setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getOpponentPlayerId() {
        return opponentPlayerId;
    }

    public void setOpponentPlayerId(String opponentPlayerId) {
        this.opponentPlayerId = opponentPlayerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getOpponentTeamId() {
        return opponentTeamId;
    }

    public void setOpponentTeamId(String opponentTeamId) {
        this.opponentTeamId = opponentTeamId;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
