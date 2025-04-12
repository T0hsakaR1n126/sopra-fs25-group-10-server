package ch.uzh.ifi.hase.soprafs24.constant;

public enum GameRoleType {
    PLAYER("Player"),
    GAMEOWNER("Game Owner"),
    OPPONENT("Opponent");;

    private final String description;

    GameRoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
