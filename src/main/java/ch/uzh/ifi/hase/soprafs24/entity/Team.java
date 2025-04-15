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
    private String teamName = "Not yet assigned";
    
    @Enumerated(EnumType.STRING)
    private TeamStatus teamStatus;
    
    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer desiredTeamSize;
    
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
    
    public Integer getDesiredTeamSize() {
        return desiredTeamSize;
    }
    
    public void setDesiredTeamSize(Integer desiredTeamSize) {
        this.desiredTeamSize = desiredTeamSize;
    }
    
    @Transient
    public Integer getTeamSize() {
        return players.size();
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
    
    public Integer getScore() {
        return players.stream().mapToInt(Player::getScore).sum();
    }
    
    //callback to set the team name after persisting the team entity
    @PostPersist
    private void setTeamNameAfterPersist() {
        this.teamName = "Team " + this.teamId;
    }

    //team score calculation
    public Integer getTotalScore() {
        return players.stream()
            .mapToInt(Player::getScore)
            .sum();
    }
}
