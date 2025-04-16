package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;
import java.util.Map;

public class GameHintDTO {
    private Long gameId;
    private int countryNumber;
    private int hintNumber;
    private List<Map<String, Object>> hints; 
    
    
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getCountryNumber() {
        return countryNumber;
    }

    public void setCountryNumber(int countryNumber) {
        this.countryNumber = countryNumber;
    }

    public int getHintNumber() {
        return hintNumber;
    }

    public void setHintNumber(int hintNumber) {
        this.hintNumber = hintNumber;
    }

    public List<Map<String, Object>> getHints() {
        return hints;
    }

    public void setHints(List<Map<String, Object>> hints) {
        this.hints = hints;
    }
}