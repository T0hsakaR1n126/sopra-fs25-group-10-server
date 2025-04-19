package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class UserHistoryDTO {
    private GameGetDTO game;
    private List<String> players;
    private int correctAnswers;

    // Getters and Setters
    public GameGetDTO getGame() {
        return game;
    }

    public void setGame(GameGetDTO game) {
        this.game = game;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
