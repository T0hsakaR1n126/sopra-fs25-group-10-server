package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import java.util.List;
import java.util.Map;

public class GamePostDTO {
    
    // private Long gameId;
    private Long userId;
    
    private String gameName;
    
    private Long ownerId;
    
    private Integer maxPlayersNumber;
    
    private Integer time;
    
    private GameMode modeType;
    
    private GameAccessType accessType;
    
    private String password;
    
    // private Integer hintUsingNumber;
    
    // private Country submitAnswer;
    
    // private Map<Long, Integer> scoreMap;
    
    // private Map<Long, Integer> correctAnswersMap;
    
    // private Map<Long, Integer> totalQuestionsMap;
    
    // public Long getGameId() {
    //     return gameId;
    // }
    
    // public void setGameId(Long gameId) {
    //     this.gameId = gameId;
    // }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Integer getMaxPlayersNumber() {
        return maxPlayersNumber;
    }
    
    public void setPlayersNumber(Integer maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }
    
    public Integer getTime() {
        return time;
    }
    
    public void setTime(Integer time) {
        this.time = time;
    }
    
    
    public GameMode getModeType() {
        return modeType;
    }
    
    public void setModeType(GameMode modeType) {
        this.modeType = modeType;
    }
    
    public GameAccessType getAccessType() {
        return accessType;
    }
    
    public void setAccessType(GameAccessType accessType) {
        this.accessType = accessType;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}