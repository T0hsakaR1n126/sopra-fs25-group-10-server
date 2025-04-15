package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
* Internal User Representation
* This class composes the internal representation of the user and defines how
* the user is stored in the database.
* Every variable will be mapped into a database field with the @Column
* annotation
* - nullable = false -> this cannot be left empty
* - unique = true -> this value must be unqiue across the database -> composes
* the primary key
*/
@Entity
@Table(name = "GAME")
public class Game implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long gameId;
    
    @Column(nullable = false)
    private String gameName;
    
    @Column(nullable = true)
    private Long ownerId;

    @Column(nullable = true)
    private Long userId;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "scoreboard", joinColumns = @JoinColumn(name = "gameId"))
    @MapKeyColumn(name = "userId")
    @Column(name = "userscore")
    private Map<Long, Integer> scoreBoard = new HashMap<>();
    
    @Column(nullable = false)
    private Integer maxHints;
    
    @Column(nullable = true)
    private Integer maxPlayersNumber;
    
    @Column(nullable = false)
    private GameStatus gameStatus;

    @Column(nullable = false)
    private GameAccessType accessType;

    @Column(nullable = false)
    private int currentPlayersNumber;
    
    @Column(nullable = false)
    private int time;
    
    @Column(nullable = false)
    private LocalDateTime gameCreationDate;
    
    @Column(nullable = false)
    private GameMode modeType;
    
    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;
    
    @Column(nullable = true)
    private Integer finalScore;
    
    @ElementCollection
    @CollectionTable(name = "correct_answers", joinColumns = @JoinColumn(name = "gameId"))
    @MapKeyColumn(name = "userId")
    @Column(name = "correct")
    private Map<Long, Integer> correctAnswersMap = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "total_questions", joinColumns = @JoinColumn(name = "gameId"))
    @MapKeyColumn(name = "userId")
    @Column(name = "total")
    private Map<Long, Integer> totalQuestionsMap = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "result_summaries", joinColumns = @JoinColumn(name = "gameId"))
    @MapKeyColumn(name = "userId")
    @Column(name = "summary")
    private Map<Long, String> resultSummaryMap = new HashMap<>();
    
    public Long getGameId() {
        return gameId;
    }
    
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    //addding user id for join purposes
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public GameAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(GameAccessType accessType) {
        this.accessType = accessType;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public GameStatus getGameStatus() {
        return gameStatus;
    }
    
    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    //add player
    public void addPlayer(Player player) {
        players.add(player);
        player.setGame(this); // set back-reference
    }
    //remove player
    public void removePlayer(Player player) {
        players.remove(player);
        player.setGame(null);
    }
    
    //get scoreBoard
    public Map<Long, Integer> getScoreBoard() {
        return scoreBoard;
    }
    
    public void setScoreBoard(Map<Long, Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
    
    // update scoreBoard
    public void updateScore(Long userId, int score) {
        scoreBoard.put(userId, score);
    }
    
    // get specific user's score
    public Integer getScore(Long userId) {
        return scoreBoard.get(userId);
    }
    
    // remove specific user's score
    public Integer removeScore(Long userId) {
        return scoreBoard.remove(userId);
    }
    
    public int getMaxHints() {
        return maxHints;
    }
    
    public void setMaxHints(int maxHints) {
        this.maxHints = maxHints;
    }
    
    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }
    
    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }
    
    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }
    
    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }
    
    public int getTime() {
        return time;
    }
    
    public void setTime(int time) {
        this.time = time;
    }
    
    public LocalDateTime getGameCreationDate() {
        return gameCreationDate;
    }
    
    public void setGameCreationDate(LocalDateTime gameCreationDate) {
        this.gameCreationDate = gameCreationDate;
    }
    
    public GameMode getModeType() {
        return modeType;
    }
    
    public void setModeType(GameMode modeType) {
        this.modeType = modeType;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getFinalScore() {
        return finalScore;
    }
    
    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }
    
    public Map<Long, String> getResultSummaryMap() {
        return resultSummaryMap;
    }
    
    public void setResultSummaryMap(Map<Long, String> resultSummaryMap) {
        this.resultSummaryMap = resultSummaryMap;
    }
    
    public Map<Long, Integer> getTotalQuestionsMap() {
        return totalQuestionsMap;
    }
    
    public void setTotalQuestionsMap(Map<Long, Integer> totalQuestionsMap) {
        this.totalQuestionsMap = totalQuestionsMap;
    }
    
    public Map<Long, Integer> getCorrectAnswersMap() {
        return correctAnswersMap;
    }
    
    public void setCorrectAnswersMap(Map<Long, Integer> correctAnswersMap) {
        this.correctAnswersMap = correctAnswersMap;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}