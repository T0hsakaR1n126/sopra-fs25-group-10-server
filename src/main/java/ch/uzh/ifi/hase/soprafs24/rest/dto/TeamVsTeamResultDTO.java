package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class TeamVsTeamResultDTO {
    private String winner;
    private String loser;
    private Integer team1Score;
    private Integer team2Score;
    private List<PlayerDTO> scoreboard;
    private boolean draw;

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }
    
    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public Integer getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(Integer team1Score) {
        this.team1Score = team1Score;
    }

    public Integer getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(Integer team2Score) {
        this.team2Score = team2Score;
    }

    public List<PlayerDTO> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(List<PlayerDTO> scoreboard) {
        this.scoreboard = scoreboard;
    }
}