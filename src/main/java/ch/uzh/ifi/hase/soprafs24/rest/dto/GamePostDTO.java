package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class GamePostDTO {

    private Long gameId;

    private String gameName;

    private String owner;

    private List<String> players;
    
    private int playersNumber;

    private int time;
  
    private String modeType;

    private String lockType;

    private String password;

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
}