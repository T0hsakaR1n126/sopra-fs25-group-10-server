package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;

public abstract class GameSummaryDTO {
    private Long gameId;
    private GameMode modeType;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public GameMode getModeType() {
        return modeType;
    }

    public void setModeType(GameMode modeType) {
        this.modeType = modeType;
    }
}