package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameRoleType;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long playerId;
    
    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)  // nullable for guests
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerStatus playerStatus = PlayerStatus.WAITING;
    
    @Column(nullable = false)
    private int score = 0;
    
    @Column(nullable = false)
    private boolean isGuest = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameRoleType role = GameRoleType.PLAYER;
    
    @ManyToOne
    @JoinColumn(name = "teamId", nullable = true)
    private Team team;
    
    @Column(nullable = true, unique = true)
    private String playerName;
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    @Column(nullable = false)
    private GameMode modeType = GameMode.SOLO;
    
    @Column(nullable = false)
    private int totalHints;
    
    @Column(nullable = false)
    private int hintsLeft;
    
    public Player(User user, Game game) {
        if (user != null) {
            this.user = user;
            this.isGuest = false;
        } else {
            this.isGuest = true;
            this.user = null;
            this.setRole(GameRoleType.GUEST);
        }
        
        this.game = game;
        this.modeType = game.getModeType(); // inherit game mode
        this.totalHints = game.getMaxHints(); // from game settings
    }
    
    // Methods
    public void assignPlayerName() {
        if (this.user != null) {
            this.playerName = this.user.getUsername();
        } else {
            if (this.playerId != null) {
                this.playerName = "Guest_" + this.playerId;
            } else {
                throw new IllegalStateException("Player ID must be assigned before setting guest name.");
            }
        }
    }

    public Long getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }
    
    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public boolean isGuest() {
        return isGuest;
    }
    
    public void setGuest(boolean guest) {
        isGuest = guest;
    }
    
    public GameRoleType getRole() {
        return role;
    }
    
    public void setRole(GameRoleType role) {
        this.role = role;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public GameMode getModeType() {
        return modeType;
    }
    
    public void setModeType(GameMode modeType) {
        this.modeType = modeType;
    }
    
    public int getTotalHints() {
        return totalHints;
    }
    
    public void getHint() {
        this.hintsLeft= this.totalHints-1;
    }
    
    public void useHint() {
        if (hintsLeft > 0) {
            this.hintsLeft= this.totalHints-1;
        } else {
            throw new IllegalStateException("No more hints available");
        }
    }
    
    public int getHintsLeft() {
        return hintsLeft;
    }
}
