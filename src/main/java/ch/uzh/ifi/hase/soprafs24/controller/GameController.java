package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreateResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyJoinGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerAuthDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStartDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
* User Controller
* This class is responsible for handling all REST request that are related to
* the user.
* The controller will receive the request and delegate the execution to the
* UserService and finally return the result.
*/

@RestController
public class GameController {
    
    private final GameService gameService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameCreateResponseDTO createGame(@RequestBody GamePostDTO gamePostDTO) {
        Game gameToCreate = DTOMapper.INSTANCE.convertGamePostDTOtoGameEntity(gamePostDTO);
        return gameService.createGame(gameToCreate);
    }
    
    @GetMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getGameLobby() {
        List<Game> allGames = gameService.getAllGames();
        
        List<GameGetDTO> gameLobbyGetDTOs = allGames.stream()
        .filter(game -> game.getModeType() == GameMode.ONE_VS_ONE || game.getModeType() == GameMode.TEAM_VS_TEAM)
        .map(DTOMapper.INSTANCE::convertGameEntityToGameGetDTO)
        .collect(Collectors.toList());
        
        return gameLobbyGetDTOs;
    }
    
    
    @GetMapping("/game/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameReady(@PathVariable Long gameId) {
        Game gameSelected = gameService.getGameByGameId(gameId);
        if (gameSelected == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        return DTOMapper.INSTANCE.convertGameEntityToGameGetDTO(gameSelected);
    }
    
    @PostMapping("/lobby/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerAuthDTO joinGame(@PathVariable Long gameId,
    @RequestBody(required = false) LobbyJoinPostDTO joinDTO) {
        Long userId = joinDTO != null ? joinDTO.getUserId() : null;
        String password = joinDTO != null ? joinDTO.getPassword() : null;
        
        return DTOMapper.INSTANCE.convertPlayerToPlayerAuthDTO(gameService.userJoinGame(gameId, userId, password));
    }
    
    @PutMapping("/lobbyOut/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void exitGame(@PathVariable Long playerId) {
        gameService.playerExitGame(playerId);
    }
    
    //   @GetMapping("/ready/{gameId}")
    //   @ResponseStatus(HttpStatus.OK)
    //   @ResponseBody
    //   public List<UserGetDTO> getGamePlayers(@PathVariable Long gameId) {
    //     List<User> players = gameService.getGamePlayers(gameId);
    
    //     List<UserGetDTO> allPlayersDTOs = new ArrayList<>();
    //     for (User player : players) {
    //       allPlayersDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(player));
    //     }
    //     return allPlayersDTOs;
    //   }
    
    @PutMapping("/start/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void startGame(@PathVariable Long gameId, @RequestBody  GameStartDTO player) {
        gameService.startGame(gameId, player);
    }
    
    //   @PutMapping("/games/{gameId}/end")
    //   @ResponseStatus(HttpStatus.NO_CONTENT)
    //   public void submitScores(@PathVariable Long gameId, @RequestBody GamePostDTO gamePostDTO) {
    //       gameService.submitScores(
    //           gameId,
    //           gamePostDTO.getScoreMap(),
    //           gamePostDTO.getCorrectAnswersMap(),
    //           gamePostDTO.getTotalQuestionsMap()
    //       );
    //   }
    
    //   @GetMapping("/users/{userId}/history")
    //   @ResponseStatus(HttpStatus.OK)
    //   @ResponseBody
    //   public List<GameGetDTO> getUserGameHistory(@PathVariable Long userId) {
    //     return gameService.getGamesByUser(userId);
    //   }
    
    //   @GetMapping("/leaderboard")
    //   @ResponseStatus(HttpStatus.OK)
    //   @ResponseBody
    //   public List<GameGetDTO> getLeaderboard() {
    //     return gameService.getLeaderboard();
    //   }
}