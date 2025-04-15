package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class HintPostDTO {
    private String hint;
    private String hintType;
    // private String countryCode;

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHintType() {
        return hintType;
    }

    public void setHintType(String hintType) {
        this.hintType = hintType;
    }
}
