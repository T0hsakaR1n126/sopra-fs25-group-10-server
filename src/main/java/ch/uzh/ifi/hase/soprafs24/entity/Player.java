package ch.uzh.ifi.hase.soprafs24.entity;

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
    @JoinColumn(name = "userId", nullable = true)  //nullable for guests
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

    // Constructor to handle player creation
    public Player(User user, Game game) {
        if (user != null) {
            this.user = user;
            this.isGuest = false;
        } else {
            this.isGuest = true;
            this.user = null;  // No associated user for guest
        }
        this.game = game;
        this.playerId = generatePlayerId();
    }

    // Helper method to generate player ID (whether user or guest)
    private Long generatePlayerId() {
        if (this.isGuest) {
            return (long) (Math.random() * Long.MAX_VALUE);
        } else {
            //user id from the registered user
            return this.user != null ? this.user.getUserId() : null;
        }
    }

    // Getter and Setter methods
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

    public void setGuest(boolean isGuest) {
        this.isGuest = isGuest;
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
}