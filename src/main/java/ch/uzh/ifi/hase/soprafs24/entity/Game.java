package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

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

    // Assuming you want to have a list of Player objects in the game, use @ManyToMany or @OneToMany
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();

    // Assuming each game involves teams, use @OneToMany for teams if applicable
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList<>();

    // Store scores for each player
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

    @Column(nullable = false)
    private GameStatus gameStatus = GameStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameMode modeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameAccessType accessType;

    @Column(nullable = true)
    private String password;

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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        players.add(player);  // Assuming Player has a setGame method to link back to the game
        player.setGame(this);  // Set the game for the player
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setGame(null);  // Set the game for the player to null
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
