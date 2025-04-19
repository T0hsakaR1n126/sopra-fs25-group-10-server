package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.constant.GuessResult;

public class PlayerResultDTO {

    private Country correctAnswer;
    private GuessResult guessResult;

    public Country getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(Country correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public GuessResult getGuessResult() {
        return guessResult;
    }
    public void setGuessResult(GuessResult guessResult) {
        this.guessResult = guessResult;
    }
}
