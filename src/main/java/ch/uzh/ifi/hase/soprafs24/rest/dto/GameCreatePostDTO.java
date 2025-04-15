package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameCreatePostDTO {

    private Long gameId;

    private Long userId;

    private String gameName;

    private String owner;
    
    private int maxPlayersNumber;

    private int time;
  
    private String modeType;

    private String accessType;

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

    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
      }
    
      public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
      }

      
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
    
      public String getaccessType() {
        return accessType;
      }
    
      public void setaccessType(String accessType) {
        this.accessType = accessType;
      }
    
      public String getPassword() {
        return password;
      }
    
      public void setPassword(String password) {
        this.password = password;
      }
}