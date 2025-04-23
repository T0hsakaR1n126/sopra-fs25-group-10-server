package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class HintPostDTO {
    private Long playerId;
    private String token;
    // private String hintText;
    private Integer difficulty;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }


    // public String getHintText() {
    //     return hintText;
    // }

    // public void setHintText(String hintText) {
    //     this.hintText = hintText;
    // }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
