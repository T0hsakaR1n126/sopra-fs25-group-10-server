package ch.uzh.ifi.hase.soprafs24.constant;

public enum GuessResult {
    CORRECT("CORRECT"),
    INCORRECT("INCORRECT");

    private final String result;
    GuessResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }
}
