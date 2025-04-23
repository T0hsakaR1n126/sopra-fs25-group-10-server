package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Country;

public class PlayerAnswerDTO {
    private Long playerId;
    private Country answer;
    private String token;

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


    public Country getAnswer() {
        return answer;
    }

    public void setAnswer(Country answer) {
        this.answer = answer;
    }
}
