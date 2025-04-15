package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameGuessResultDTO {
    private boolean isCorrect;
    private String correctCountry;
    private int pointsAwarded;
    
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    public String getCorrectCountry() {
        return correctCountry;
    }
    public void setCorrectCountry(String correctCountry) {
        this.correctCountry = correctCountry;
    }
    public int getPointsAwarded() {
        return pointsAwarded;
    }
    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
}