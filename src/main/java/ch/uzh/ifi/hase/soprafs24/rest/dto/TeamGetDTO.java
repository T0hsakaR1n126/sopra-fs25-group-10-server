package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class TeamGetDTO {
    private Long id;
    private String name;
    private List<PlayerGetDTO> players;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerGetDTO> players) {
        this.players = players;
    }
}
