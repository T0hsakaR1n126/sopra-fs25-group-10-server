package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @Column(nullable = false, unique = true)
    private String gameName;

    @Column(nullable = false, unique = true)
    private String owner;

    @ElementCollection
    @CollectionTable(name = "gameplayers", joinColumns = @JoinColumn(name = "gameId"))
    @Column(name = "username")
    private List<String> players = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "scoreboard", joinColumns = @JoinColumn(name = "gameId"))
    @MapKeyColumn(name = "username")
    @Column(name = "userscore")
    private Map<String, Integer> scoreBoard = new HashMap<>();
    
    @Column(nullable = false)
    private int hintsNumber;

    @Column(nullable = false)
    private int playersNumber;

    @Column(nullable = false)
    private int realPlayersNumber;

    @Column(nullable = false)
    private int time;
    
    @Column(nullable = true)
    private String gameCreationDate;

    @Enumerated(EnumType.STRING)  // Enums are stored as Strings in DB
    @Column(nullable = false)
    private GameMode modeType;  // Game mode (1v1, Team vs Team, Solo)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameAccessType accessType;  // Public or Private game

    @Column(nullable = true)
    private String password;  // Used for private games

    // Getter and Setter for GameMode
    public GameMode getModeType() {
        return modeType;
    }

    public void setModeType(GameMode modeType) {
        this.modeType = modeType;
    }

    // Getter and Setter for GameAccessType
    public GameAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(GameAccessType accessType) {
        this.accessType = accessType;
    }

    // Getter and Setter for password (only relevant for private games)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter methods for other fields
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(User player) {
        players.add(player.getUsername());
        player.setGame(this);
    }

    public void removePlayer(User player) {
        players.remove(player.getUsername());
        player.setGame(null);
    }

    public Map<String, Integer> getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(Map<String, Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void updateScore(String username, int score) {
        scoreBoard.put(username, score);
    }

    public Integer getScore(String username) {
        return scoreBoard.get(username);
    }

    public Integer removeScore(String username) {
        return scoreBoard.remove(username);
    }

    public int getHintsNumber() {
        return hintsNumber;
    }

    public void setHintsNumber(int hintsNumber) {
        this.hintsNumber = hintsNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public int getRealPlayersNumber() {
        return realPlayersNumber;
    }

    public void setRealPlayersNumber(int realPlayersNumber) {
        this.realPlayersNumber = realPlayersNumber;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getGameCreationDate() {
        return gameCreationDate;
    }

    public void setGameCreationDate(String gameCreationDate) {
        this.gameCreationDate = gameCreationDate;
    }
}