package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;
import java.util.Map;

public class GamePostDTO {

    private Long gameId;

    private String gameName;

    private Long ownerId;

    private List<String> players;
    
    private int playersNumber;

    private int time;
  
    private String modeType;

    private String lockType;

    private String password;

    private Map<String, Integer> scoreMap;

    private Map<String, Integer> correctAnswersMap;

    private Map<String, Integer> totalQuestionsMap;

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

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getPlayersNumber() {
        return playersNumber;
      }
    
      public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
      }
      
      public int getTime() {
        return time;
      }
    
      public void setTime(int time) {
        this.time = time;
      }

    
      public String getModeType() {
        return modeType;
      }
    
      public void setModeType(String modeType) {
        this.modeType = modeType;
      }
    
      public String getLockType() {
        return lockType;
      }
    
      public void setLockType(String lockType) {
        this.lockType = lockType;
      }
    
      public String getPassword() {
        return password;
      }
    
      public void setPassword(String password) {
        this.password = password;
      }

      public Map<String, Integer> getScoreMap() {
        return scoreMap;
      }
    
      public void setScoreMap(Map<String, Integer> scoreMap) {
        this.scoreMap = scoreMap;
      }
    
      public Map<String, Integer> getCorrectAnswersMap() {
        return correctAnswersMap;
      }
    
      public void setCorrectAnswersMap(Map<String, Integer> correctAnswersMap) {
        this.correctAnswersMap = correctAnswersMap;
      }
    
      public Map<String, Integer> getTotalQuestionsMap() {
        return totalQuestionsMap;
      }
    
      public void setTotalQuestionsMap(Map<String, Integer> totalQuestionsMap) {
        this.totalQuestionsMap = totalQuestionsMap;
      }
}