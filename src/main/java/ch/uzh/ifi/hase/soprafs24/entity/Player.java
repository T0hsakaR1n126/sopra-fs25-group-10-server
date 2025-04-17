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

    @Column(nullable = false, unique = true)
    private String token;
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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
    private Integer score = 0;
    
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

    @Column(nullable = false)
    private Long totalQuestions;

    @Column(nullable = true)
    private Long correctQuestions;
    
    public Long getCorrectQuestions() {
        return correctQuestions;
    }

    public void setCorrectQuestions(Long correctQuestions){
        this.correctQuestions = correctQuestions;
    }

    @Column(nullable = true)
    private Long questionId;

    @Column(nullable = true)
    private Long skippedQuestions;
    
    @Column(nullable = true)
    private Integer currentHint;

    public Long getSkippedQuestions() {
        return skippedQuestions;
    }

    public void setSkippedQuestions(Long skippedQuestions) {
        this.skippedQuestions = skippedQuestions;
    }

    public Integer getCurrentHint() {
        return currentHint;
    }

    public void setCurrentHint(Integer currentHint) {
        this.currentHint = currentHint;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    @Column(nullable = false)
    private GameMode modeType = GameMode.SOLO;
    
    @Column(nullable = false)
    private Integer totalHints;
    
    @Column(nullable = true)
    private Integer hintsLeft;

    public Player() {
        // Default constructor
    }
    
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
    @PostPersist
    private void setPlayerNameAfterPersist() {
        if (this.user != null) {
            this.playerName = this.user.getUsername();
        } else {
            this.playerName = "Guest_" + this.playerId;
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
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public boolean isGuest() {
        return isGuest;
    }
    
    public void addScore(Integer points) {
        this.score += points;
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
    
    public Integer getTotalHints() {
        return totalHints;
    }    
 
    public void useHint() {
        if (hintsLeft > 0) {
            this.hintsLeft -= 1 ;
        } else {
            throw new IllegalStateException("No more hints available");
        }
    }
    
    public Integer getHintsLeft() {
        return hintsLeft;
    }

    public void setHintsLeft(Integer hintsLeft) {
        this.hintsLeft = hintsLeft;
    }

    public Long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
