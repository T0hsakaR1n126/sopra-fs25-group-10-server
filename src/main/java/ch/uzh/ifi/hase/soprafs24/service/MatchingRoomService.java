package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Team;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

@Service
public class MatchingRoomService {

    @Autowired
    private GameRepository gameRepository;

    // Waiting rooms for different modes
    private Map<GameMode, Queue<Player>> waitingRooms = new HashMap<>();
    private Map<GameMode, Queue<Team>> waitingTeamRooms = new HashMap<>();

    public MatchingRoomService() {
        // Initialize waiting rooms for both game modes
        waitingRooms.put(GameMode.ONE_VS_ONE, new LinkedList<>());
        waitingTeamRooms.put(GameMode.TEAM_VS_TEAM, new LinkedList<>());
    }

    // Add player to the waiting room
    public void addPlayerToWaitingRoom(Player player, GameMode mode) {
        if (mode == GameMode.ONE_VS_ONE) {
            // Add player to the waiting room for 1v1
            waitingRooms.get(GameMode.ONE_VS_ONE).offer(player);
        } else if (mode == GameMode.TEAM_VS_TEAM) {
            // Ensure the player has a team
            Team playerTeam = player.getTeam();
            if (playerTeam != null && playerTeam.getPlayers().size() > 0) {
                // Only add the team to the waiting room if it's a valid team
                waitingTeamRooms.get(GameMode.TEAM_VS_TEAM).offer(playerTeam);
            } else {
                // Handle the case where the player does not have a valid team
                // This could be an error message or another handling mechanism
                System.out.println("Player does not have a valid team for TEAM_VS_TEAM mode.");
            }
        }
    }

    // Retrieve the game by ID
    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElse(null);  // Return null or handle appropriately if not found
    }

    // Get the opponent (player or team based on mode)
    public Object getOpponent(GameMode mode) {
        if (mode == GameMode.ONE_VS_ONE) {
            // Return an opponent player if available in the waiting room
            Player opponent = waitingRooms.get(GameMode.ONE_VS_ONE).poll();
            return opponent;
        } else if (mode == GameMode.TEAM_VS_TEAM) {
            // Return an opponent team if available in the waiting room
            Team opponentTeam = waitingTeamRooms.get(GameMode.TEAM_VS_TEAM).poll();
            // Ensure the team is fully populated (you may want to adjust the criteria for a "complete" team)
            if (opponentTeam != null && opponentTeam.getPlayers().size() > 1) {
                return opponentTeam;
            } else {
                return null;  // No valid team found
            }
        }
        return null;  // Return null if no opponent is found
    }

    // Create a new game
    public Game createGame(GameMode mode, List<Player> players, List<Team> teams) {
        Game game = new Game();
        game.setModeType(mode);

        // Set players and teams based on mode
        if (mode == GameMode.ONE_VS_ONE) {
            game.setPlayers(players);  // Assuming players list is provided for 1v1 mode
        } else if (mode == GameMode.TEAM_VS_TEAM) {
            game.setTeams(teams);  // Set the teams for Team vs Team mode
        }

        // Save the game to the repository
        gameRepository.save(game);
        return game;
    }

    // Update the game status
    public void updateGameStatus(Long gameId, GameStatus status) {
        Game game = getGameById(gameId);
        if (game != null) {
            game.setGameStatus(status);
            gameRepository.save(game);  // Save the updated status to the database
        }
    }
}
