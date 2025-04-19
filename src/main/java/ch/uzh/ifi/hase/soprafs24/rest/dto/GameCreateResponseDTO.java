package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameCreateResponseDTO {
    private GameGetDTO game;
    private PlayerAuthDTO player;

    public GameGetDTO getGame() {
        return game;
    }

    public void setGame(GameGetDTO game) {
        this.game = game;
    }

    public PlayerAuthDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerAuthDTO player) {
        this.player = player;
    }
}