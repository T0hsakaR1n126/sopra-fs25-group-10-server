package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class TeamPostDTO {
    private String name;
    private List<Long> playerIds;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }
}

