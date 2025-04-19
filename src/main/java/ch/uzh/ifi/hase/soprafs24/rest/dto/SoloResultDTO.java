package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class SoloResultDTO {
    private PlayerDTO player;
    private List<PlayerDTO> scoreboard;

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public List<PlayerDTO> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(List<PlayerDTO> scoreboard) {
        this.scoreboard = scoreboard;
    }
}
