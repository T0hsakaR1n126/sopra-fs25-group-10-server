package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameHintDTO {
    private String hint;
    private int hintNumber;

    
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getHintNumber() {
        return hintNumber;
    }

    public void setHintNumber(int hintNumber) {
        this.hintNumber = hintNumber;
    }
}