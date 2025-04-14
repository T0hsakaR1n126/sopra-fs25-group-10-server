package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameJoinDTO {
    private Long userId;
    private String password;
    private String guestName;
    
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getGuestName() {
        return guestName;
    }
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
}