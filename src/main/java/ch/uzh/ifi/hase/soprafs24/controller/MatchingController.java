package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Team;
import ch.uzh.ifi.hase.soprafs24.service.MatchingRoomService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.MatchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MatchingController {

    @Autowired
    private MatchingRoomService matchingRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // For sending WebSocket messages

    // Handle player or team joining the waiting room
    @MessageMapping("/joinWaitingRoom")
    public void joinWaitingRoom(Player player) {
        // Add player or team to the waiting room based on game mode
        GameMode mode = player.getGame().getModeType();  // Assume mode is set already
        matchingRoomService.addPlayerToWaitingRoom(player, mode);

        // Notify the player that they are searching for a match
        messagingTemplate.convertAndSendToUser(player.getUser().getUsername(), "/topic/notification",
                "Searching for opponent...");

        // Notify other players about this player joining the waiting room (optional)
        messagingTemplate.convertAndSend("/topic/waitingRoom", player.getUser().getUsername() + " joined the waiting room.");
    }

    // Handle starting the game and notifying players
    @MessageMapping("/startGame")
    public void startGame(Long gameId, Long playerId) {
        // Fetch the game based on the gameId
        Game game = matchingRoomService.getGameById(gameId);  // Assuming this method exists in the service

        // Match response for both players or teams
        MatchResponseDTO matchResponseDTO = new MatchResponseDTO();
        matchResponseDTO.setGameId(gameId);

        // Get opponent based on the game mode (team or 1v1)
        GameMode mode = game.getModeType();
        Object opponent = matchingRoomService.getOpponent(mode);

        if (opponent != null) {
            if (mode == GameMode.ONE_VS_ONE) {
                // For 1v1, opponent is a player
                Player opponentPlayer = (Player) opponent;
                matchResponseDTO.setPlayerId(playerId.toString());  // Set the current player ID
                matchResponseDTO.setOpponentPlayerId(opponentPlayer.getPlayerId().toString());  // Set the opponent player ID
                
                // Notify both players about game start
                messagingTemplate.convertAndSendToUser(playerId.toString(), "/topic/gameStart", matchResponseDTO);
                messagingTemplate.convertAndSendToUser(opponentPlayer.getPlayerId().toString(), "/topic/gameStart", matchResponseDTO);

            } else if (mode == GameMode.TEAM_VS_TEAM) {
                Team opponentTeam = (Team) opponent;

                // Get player IDs for both teams
                List<Long> team1PlayerIds = game.getTeams().get(0).getPlayers().stream().map(p -> p.getPlayerId()).toList();
                List<Long> team2PlayerIds = opponentTeam.getPlayers().stream().map(p -> p.getPlayerId()).toList();
                
                // Directly set team IDs for both teams
                matchResponseDTO.setTeamId(game.getTeams().get(0).getTeamId().toString());  // Set the team 1 ID
                matchResponseDTO.setOpponentTeamId(opponentTeam.getTeamId().toString());    // Set the opponent team ID
    
                // Notify both teams about game start
                messagingTemplate.convertAndSendToUser(team1PlayerIds.get(0).toString(), "/topic/gameStart", matchResponseDTO);  // Notify team 1
                messagingTemplate.convertAndSendToUser(team2PlayerIds.get(0).toString(), "/topic/gameStart", matchResponseDTO);  // Notify team 2
            }
        }

        // Further logic to update game status and handle the start of the game
        game.setGameStatus(GameStatus.ACTIVE);
        // gameRepository.save(game);  // Uncomment if gameRepository exists to persist changes
    }
}
