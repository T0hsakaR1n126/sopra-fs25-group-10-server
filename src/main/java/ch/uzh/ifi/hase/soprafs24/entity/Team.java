package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.TeamStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    
    @Column(nullable = false)
    private int desiredTeamSize;
    
    @Column(nullable = false)
    private int TeamSize;

   //getters and setters
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
    
    public int getTeamSize() {
        return TeamSize;
    }
    
    public void setTeamSize(int teamSize) {
        this.TeamSize = players.size();
    }
    
    
    public void addPlayer(Player player) {
        this.players.add(player);
        player.setTeam(this);
    }
    
    public void removePlayer(Player player) {
        this.players.remove(player);
        player.setTeam(null);
    }
    
    public boolean isTeamFull() {
        return getTeamSize() >= desiredTeamSize;
    }
    
    //gets scores of the team overall
    public int getScore() {
        return players.stream().mapToInt(Player::getScore).sum();
    }
}
