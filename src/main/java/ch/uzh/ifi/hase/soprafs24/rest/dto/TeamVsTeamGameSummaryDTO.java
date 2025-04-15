package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Map;

public class TeamVsTeamGameSummaryDTO extends GameSummaryDTO {
    private Map<TeamDTO, Integer> teamScores;

    public Map<TeamDTO, Integer> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(Map<TeamDTO, Integer> teamScores) {
        this.teamScores = teamScores;
    }
}