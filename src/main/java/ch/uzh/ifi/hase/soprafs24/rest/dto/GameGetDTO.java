package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GameGetDTO {

    private String ownerName;

    private Long ownerId;

    private Long userId;

    private String username;

    private Integer totalScore;

    private LocalDateTime startTime;

    private Long gameId;

    private GameStatus gameStatus;

    private Map<Long, Integer> scoreBoard;

    private String gameName;

    private Integer time;

    private GameAccessType accessType;

    private Integer maxPlayersNumber;

    private Integer currentPlayersNumber;

    private String password;

    private String modeType;

    private LocalDateTime endTime;

    private Integer finalScore;

    private Integer maxHints;

    private LocalDateTime gameCreationDate;

    private String resultSummary;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private List<Map<String, Object>> hints;

    private LocalDateTime starTime;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
    
    public LocalDateTime getStarTime() {
        return starTime;
    }

    public void setStarTime(LocalDateTime starTime) {
        this.starTime = starTime;
    }

    public LocalDateTime getGameCreationDate() {
        return gameCreationDate;
    }

    public void setGameCreationDate(LocalDateTime gameCreationDate) {
        this.gameCreationDate = gameCreationDate;
    }

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

    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }   
    
    public GameAccessType getAccessType() {
        return accessType;
    }
    
    public void setAccessType(GameAccessType accessType) {
        this.accessType = accessType;
    }

    public Integer getMaxPlayersNumber() {
        return maxPlayersNumber;
    }
    
    public void setMaxPlayersNumber(Integer maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }     

      public Integer getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }
    
    public void setCurrentPlayersNumber(Integer currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }   

    public Map<Long, Integer> getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(Map<Long, Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public Integer getTime() {
        return time;
    }
    
    public void setTime(Integer time) {
        this.time = time;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getModeType() {
        return modeType;
    }
    
    public void setModeType(String modeType) {
        this.modeType = modeType;
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
    
    public String getResultSummary() {
        return resultSummary;
    }
    
    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Integer getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
      
    public void setHints(List<Map<String, Object>> hints){
        this.hints = hints;
    }
  
    public List<Map<String, Object>> getHints(){
        return hints;
    }

    public Integer getMaxHints() {
        return maxHints;
    }

    public void setMaxHints(Integer maxHints) {
        this.maxHints = maxHints;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}

