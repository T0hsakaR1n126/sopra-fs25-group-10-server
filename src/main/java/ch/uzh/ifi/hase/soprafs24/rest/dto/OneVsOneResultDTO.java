package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class OneVsOneResultDTO {
    private PlayerDTO winner;
    private PlayerDTO loser;
    private List<PlayerDTO> scoreboard;
    private boolean draw;

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public PlayerDTO getWinner() {
        return winner;
    }

    public void setWinner(PlayerDTO winner) {
        this.winner = winner;
    }

    public PlayerDTO getLoser() {
        return loser;
    }

    public void setLoser(PlayerDTO loser) {
        this.loser = loser;
    }

    public List<PlayerDTO> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(List<PlayerDTO> scoreboard) {
        this.scoreboard = scoreboard;
    }
}