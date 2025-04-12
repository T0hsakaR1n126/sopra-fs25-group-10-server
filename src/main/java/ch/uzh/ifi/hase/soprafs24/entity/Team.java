package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.TeamStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a team in the game.
 * Each team can have a set of players and is associated with a game.
 */
@Entity
@Table(name = "TEAM")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private String teamName;

    @Enumerated(EnumType.STRING)
    private TeamStatus teamStatus;

    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private int desiredTeamSize;  // Desired team size (set by the team creator)

    // Getters and setters
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

    public TeamStatus getTeamStatus() {
        return teamStatus;
    }

    public void setTeamStatus(TeamStatus teamStatus) {
        this.teamStatus = teamStatus;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getDesiredTeamSize() {
        return desiredTeamSize;
    }

    public void setDesiredTeamSize(int desiredTeamSize) {
        this.desiredTeamSize = desiredTeamSize;
    }

    // Method to add a player to the team
    public void addPlayer(Player player) {
        this.players.add(player);
        player.setTeam(this);
    }

    // Method to remove a player from the team
    public void removePlayer(Player player) {
        this.players.remove(player);
        player.setTeam(null);
    }

    // Method to get the number of players in the team
    public int getNumberOfPlayers() {
        return players.size();
    }

    // Method to check if the team has reached the desired size
    public boolean isTeamFull() {
        return getNumberOfPlayers() >= desiredTeamSize;
    }
}
