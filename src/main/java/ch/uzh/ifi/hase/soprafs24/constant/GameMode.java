package ch.uzh.ifi.hase.soprafs24.constant;

public enum GameMode {
    ONE_VS_ONE("1v1"),
    TEAM_VS_TEAM("Team vs Team"),
    SOLO("Solo");

    private final String description;

    GameMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
