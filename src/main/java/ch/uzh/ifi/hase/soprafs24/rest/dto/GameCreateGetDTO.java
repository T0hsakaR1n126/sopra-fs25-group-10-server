package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Map;

public class GameCreateGetDTO {
    
    private String owner;
    
    private Long gameId;
    
    private Map<String, String> scoreBoard;
    
    private String gameName;
    
    private String accessType;
    
    private int maxPlayersNumber;
    
    private int currentPlayersNumber;
    
    private String password;
    
    private String modeType;
    
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
    
    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }
    
    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }     
    
    public String getaccessType() {
        return accessType;
    }
    
    public void setaccessType(String accessType) {
        this.accessType = accessType;
    }
    
    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }
    
    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }   
    
    public Map<String, String> getScoreBoard() {
        return scoreBoard;
    }
    
    public void setScoreBoard(Map<String, String> scoreBoard) {
        this.scoreBoard = scoreBoard;
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
}

