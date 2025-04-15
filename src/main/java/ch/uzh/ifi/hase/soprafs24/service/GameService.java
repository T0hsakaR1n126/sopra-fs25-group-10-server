package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameHints;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UtilService;
import ch.uzh.ifi.hase.soprafs24.constant.Country;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* User Service
* This class is the "worker" and responsible for all functionality related to
* the user
* (e.g., it creates, modifies, deletes, finds). The result will be passed back
* to the caller.
*/
@Service
@Transactional
public class GameService {
    
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    
    private Map<Country, List<Map<String, Object>>> generatedHintsA;
    private Map<Country, List<Map<String, Object>>> generatedHintsB;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    UtilService utilService = new UtilService();
    
    public GameService(
    @Qualifier("gameRepository") GameRepository gameRepository,
    @Qualifier("userRepository") UserRepository userRepository,
    @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }
    
    public List<Game> getAllGames() {
        return this.gameRepository.findAll();
    }
    
    //checks
    public void checkIfOwnerExists(Long userId){
        User userwithUserId = userRepository.findByUserId(userId);
        if(userwithUserId == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner doesn't exists! Please try an existed ownername");
        }
    }
    
    public void checkIfGameHaveSameOwner(Long ownerId) {
        Game existingGame = gameRepository.findByOwnerId(ownerId);
        if (existingGame != null && !existingGame.getGameStatus().equals(GameStatus.FINISHED)) {
            throw new IllegalStateException("User already has an active game session.");
        }
    }
    // public void checkIfGameNameExists(String gameName){
    //     Game gameWithSameName = gameRepository.findBygameName(gameName);
    //     if(gameWithSameName != null){
    //         throw new ResponseStatusException(HttpStatus.CONFLICT, "GameName exists! Please try a new one!");
    //     }
    // }
    
    // Game Creation Service
    public Game createGame(Game gameToCreate) {
        
        if (gameToCreate.getModeType() == null) {
            throw new IllegalArgumentException("Game mode must be specified.");
        }
        
        if (gameToCreate.getModeType() == GameMode.SOLO) {
            Integer maxPlayersNumber = gameToCreate.getMaxPlayersNumber();
            if (maxPlayersNumber == null || maxPlayersNumber != 1) {
                throw new IllegalArgumentException("For SOLO games, the number of players must be exactly 1.");
            }
        } else if (gameToCreate.getModeType() == GameMode.ONE_VS_ONE) {
            if (gameToCreate.getMaxPlayersNumber() != 2) {
                throw new IllegalArgumentException("For 1v1 games, the number of players must be exactly 2.");
            }
        }
        
        // Create Game
        Game gameCreated = new Game();
        gameCreated.setModeType(gameToCreate.getModeType());
        gameCreated.setGameName(gameToCreate.getGameName());
        gameCreated.setTime(gameToCreate.getTime());
        gameCreated.setMaxPlayersNumber(gameToCreate.getMaxPlayersNumber());
        gameCreated.setCurrentPlayersNumber(1); // First player is the owner
        gameCreated.setMaxHints(GameHints.MAX_HINTS);
        gameCreated.setAccessType(gameToCreate.getAccessType());
        gameCreated.setGameStatus(GameStatus.WAITING);
        gameCreated.setGameCreationDate(LocalDateTime.now());
        gameCreated.setScoreBoard(new HashMap<>());
        
        Long ownerId = gameToCreate.getOwnerId();
        
        if (gameToCreate.getModeType() != GameMode.SOLO) {
            if (ownerId == null) {
                throw new IllegalArgumentException("Owner ID must be provided for non-solo games.");
            }
            checkIfOwnerExists(ownerId);
            checkIfGameHaveSameOwner(ownerId);
        }
        
        // Save game first
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush(); // So we can use gameCreated.getGameId() later safely
        
        // Create owner player
        Player ownerPlayer;
        if (ownerId != null) {
            User ownerUser = userRepository.findByUserId(ownerId);
            checkIfOwnerExists(ownerId);
            checkIfGameHaveSameOwner(ownerId);
            ownerPlayer = new Player(ownerUser, gameCreated);
        } else {
            ownerPlayer = new Player(null, gameCreated);
        }
        
        ownerPlayer.setPlayerStatus(PlayerStatus.WAITING);
        ownerPlayer = playerRepository.save(ownerPlayer);
        playerRepository.flush(); // Ensure ID is generated before assignPlayerName()
        
        ownerPlayer.assignPlayerName();
        ownerPlayer = playerRepository.save(ownerPlayer);
        
        gameCreated.addPlayer(ownerPlayer);
        gameCreated.setOwnerId(ownerId);
        
        if (gameToCreate.getAccessType() == GameAccessType.PRIVATE) {
            gameCreated.setPassword(gameToCreate.getPassword());
        }
        
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        playerRepository.flush();
        
        log.debug("Added new player: {} to game: {}", ownerPlayer.getPlayerId(), gameCreated.getGameId());
        log.debug("Created new Game: {}", gameCreated);
        return gameCreated;
    }
    
    public Game getGameByGameId(Long gameId){
        return gameRepository.findBygameId(gameId);
    }
    
    public void userJoinGame(Long gameId, Long userId, String password) {
        Game targetGame = gameRepository.findBygameId(gameId);
        
        if (targetGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        }
        
        if (targetGame.getModeType() == GameMode.SOLO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't join a solo game!");
        }
        
        if (targetGame.getGameStatus() != GameStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't join this game because it is already started!");
        }
        // Check if the game is already full
        if (targetGame.getCurrentPlayersNumber() >= targetGame.getMaxPlayersNumber()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't join this game because it is full!");
        }
        
        // Check password if game is private
        if (targetGame.getAccessType() == GameAccessType.PRIVATE) {
            if (!Objects.equals(password, targetGame.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password! You can't join the game!");
            }
        }
        
        Player newPlayer;
        
        if (userId != null) {
            // Validate user existence
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            
            newPlayer = new Player(user, targetGame);
            newPlayer.setPlayerStatus(PlayerStatus.WAITING);
            
            newPlayer = playerRepository.save(newPlayer);
            playerRepository.flush();
            newPlayer.assignPlayerName();
            // Optional: Assign ownerId if not already set
            if (targetGame.getOwnerId() == null) {
                targetGame.setOwnerId(userId);
            }
        } else {
            // Create guest player
            newPlayer = new Player(null, targetGame);
            newPlayer.setPlayerStatus(PlayerStatus.WAITING);
            playerRepository.save(newPlayer);
        }
        
        targetGame.addPlayer(newPlayer);
        targetGame.setCurrentPlayersNumber(targetGame.getCurrentPlayersNumber() + 1);
        
        gameRepository.save(targetGame);
        gameRepository.flush();
        playerRepository.flush();
        
        // Notify via WebSocket
        List<Player> players = playerRepository.findByGame_GameId(targetGame.getGameId());
        messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
        
        log.info("Player {} joined game {}", newPlayer.getPlayerId(), targetGame.getGameId());
        
        // If the game is full, notify everyone that the game is full and ready to start
        if (targetGame.getCurrentPlayersNumber() == targetGame.getMaxPlayersNumber()) {
            messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/status", 
            "Players full! Let's play!");
            log.info("Game {} is full. Ready to play!", targetGame.getGameId());
        }
        
        log.info("Player {} joined game {}", newPlayer.getPlayerId(), targetGame.getGameId());
    }
    
    public void playerExitGame(Long playerId) {
        Player exitingPlayer = playerRepository.findByPlayerId(playerId);
        Game targetGame = exitingPlayer.getGame();
        
        if (targetGame == null) {
            log.warn("Player not associated with any game.");
            return;
        }
        
        boolean isOwner = exitingPlayer.getUser() != null &&
        exitingPlayer.getUser().getUserId().equals(targetGame.getOwnerId());
        
        // Remove player from game
        targetGame.removePlayer(exitingPlayer);
        targetGame.setCurrentPlayersNumber(targetGame.getCurrentPlayersNumber() - 1);
        
        // Detach player from game and save
        exitingPlayer.setGame(null);
        playerRepository.save(exitingPlayer);
        playerRepository.flush();
        
        // Check if the game still has players
        if (targetGame.getPlayers().isEmpty()) {
            gameRepository.delete(targetGame);
            gameRepository.flush();
            log.info("Game deleted due to no players remaining.");
            return;
        }
        
        // If exiting player was the owner, assign new owner
        if (isOwner) {
            Optional<Player> newOwner = targetGame.getPlayers().stream()
            .filter(p -> p.getUser() != null && p.getUser().getUserId() != null)
            .findFirst();
            
            if (newOwner.isPresent()) {
                targetGame.setOwnerId(newOwner.get().getUser().getUserId());
                log.info("Ownership transferred to player with userId {}", newOwner.get().getUser().getUserId());
            } else {
                // No eligible owner left, delete game
                gameRepository.delete(targetGame);
                gameRepository.flush();
                log.info("Game deleted due to no eligible owner.");
                return;
            }
        }
        
        gameRepository.save(targetGame);
        gameRepository.flush();
        
        // Notify all players via WebSocket
        List<Player> players = playerRepository.findByGame_GameId(targetGame.getGameId());
        messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
        log.info("Player exited. WebSocket update sent.");
    }
    
    public void startGame(Long gameId){
        Game gameToStart = gameRepository.findBygameId(gameId);
        
        //set time
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
        gameToStart.setStartTime(LocalDateTime.now());
        
        //set scoreBoard
        (gameToStart.getScoreBoard()).put(gameToStart.getOwnerId(), 0);
        for (Long userId : gameToStart.getPlayers()) {
            User player = userRepository.findByUserId(userId);
            if (player == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, (userRepository.findByUserId(userId)).getUsername() + " is not found");
            }
            (gameToStart.getScoreBoard()).put(userId, 0); 
        }
        
        generatedHintsA = utilService.generateClues(gameToStart.getMaxHints());
        generatedHintsB = utilService.generateClues(gameToStart.getMaxHints());
        
        GameGetDTO gameHintDTO = new GameGetDTO();
        
        gameHintDTO.setHints(generatedHintsA.values().iterator().next());
        gameHintDTO.setTime(gameToStart.getTime());
        
        messagingTemplate.convertAndSend("/topic/start/" + gameId + "/hints", gameHintDTO);
        log.info("websocket send!");
        
    }
    
    // public List<User> getGamePlayers(Long gameId){
    //     Game gameJoined = gameRepository.findBygameId(gameId);
    //     List<Long> allPlayers = gameJoined.getPlayers();
    
    //     List<User> players = new ArrayList<>();
    //     for (Long userId : allPlayers) {
    //         players.add(userRepository.findByUserId(userId));
    //     }
    //     return players;
    //     //
    //     //      List<UserGetDTO> allPlayersDTOs = new ArrayList<>();
    //     //
    //     //      for (Long userId : allPlayers) {
    //     //        allPlayersDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userRepository.findByUserId(userId)));
    //     //      }
    //     //      return allPlayersDTOs;
    
    // }
    
    public GameGetDTO processingAnswer(GamePostDTO gamePostDTO, Long userId){
    
        //judge right or wrong and update game
        Game targetGame = gameRepository.findBygameId(gamePostDTO.getGameId());
        //sum up total questions
        Map<Long, Integer> totalQuestionsMap = targetGame.getTotalQuestionsMap();
        if (totalQuestionsMap.containsKey(userId)) {
            int totalQuestions = totalQuestionsMap.get(userId);
            totalQuestionsMap.put(userId, totalQuestions + 1);
            targetGame.setTotalQuestionsMap(totalQuestionsMap); 
        } else {
            totalQuestionsMap.put(userId, 1);
            targetGame.setTotalQuestionsMap(totalQuestionsMap); 
        }
    
        if(gamePostDTO.getSubmitAnswer() == generatedHintsA.keySet().iterator().next()){
            //sum up correct answers
            Map<Long, Integer> currentCorrectAnswersMap = targetGame.getCorrectAnswersMap();
            if (currentCorrectAnswersMap.containsKey(userId)) {
                int currentCorrect = currentCorrectAnswersMap.get(userId);
                currentCorrectAnswersMap.put(userId, currentCorrect + 1); 
                targetGame.setCorrectAnswersMap(currentCorrectAnswersMap);
            } else {
                currentCorrectAnswersMap.put(userId, 1);
                targetGame.setCorrectAnswersMap(currentCorrectAnswersMap);
            }
            //sum up total score
            Map<Long, Integer> scoreBoardMap = targetGame.getScoreBoard();
            int currentscore = scoreBoardMap.get(userId);
            scoreBoardMap.put(userId, currentscore + (100-(gamePostDTO.getHintUsingNumber()-1))); 
            targetGame.setScoreBoard(scoreBoardMap);
        } 
        gameRepository.save(targetGame);
        gameRepository.flush();
    
        generatedHintsA=generatedHintsB;
        generatedHintsB = utilService.generateClues(targetGame.getHintsNumber());
    
        GameGetDTO gameHintDTO = new GameGetDTO();
    
        gameHintDTO.setHints(generatedHintsA.values().iterator().next());
        return gameHintDTO; 
    
    }
    
    // public void submitScores(Long gameId,Map<Long, Integer> scoreMap, Map<Long, Integer> correctAnswersMap, Map<Long, Integer> totalQuestionsMap) {
    //     Game game = gameRepository.findBygameId(gameId);
    
    //     if (game == null) {
    //         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
    //     }
    
    //     // update all users
    //     for (Long userId : scoreMap.keySet()) {
    //         Integer score = scoreMap.get(userId);
    //         Integer correct = correctAnswersMap.getOrDefault(userId, 0);
    //         Integer total = totalQuestionsMap.getOrDefault(userId, 0);
    //         String summary = correct + " of " + total + " correct";
    
    //         game.updateScore(userId, score);
    //         game.getCorrectAnswersMap().put(userId, correct);
    //         game.getTotalQuestionsMap().put(userId, total);
    //         game.getResultSummaryMap().put(userId, summary);
    
    //     }
    
    //     // end
    //     if (game.getScoreBoard().size() >= game.getCurrentPlayersNumber()) {
    //         game.setEndTime(LocalDateTime.now());
    //         log.info("Game " + gameId + " ended automatically. All players submitted scores.");
    //     }
    
    //     gameRepository.save(game);
    // }
    
    // public List<GameGetDTO> getGamesByUser(Long userId) {
    //     User user = userRepository.findById(userId)
    //     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    //     String username = user.getUsername();
    
    //     return gameRepository.findByPlayersContaining(username).stream()
    //     .filter(game -> game.getEndTime() != null)
    //     .map(game -> {
    //         GameGetDTO dto = DTOMapper.INSTANCE.convertGameEntityToGameGetDTO(game);
    //         dto.setCorrectAnswers(game.getCorrectAnswersMap().get(userId));
    //         dto.setTotalQuestions(game.getTotalQuestionsMap().get(userId));
    //         dto.setResultSummary(game.getResultSummaryMap().get(userId));
    //         return dto;
    //     })
    //     .collect(Collectors.toList());
    // }
    
    //     public List<GameGetDTO> getLeaderboard() {
    //     List<Game> allGames = gameRepository.findAll();
    //     Map<Long, Integer> userScoreMap = new HashMap<>();
    
    //     for (Game game : allGames) {
    //         if (game.getEndTime() == null || game.getScoreBoard() == null) {
    //             continue;
    //         }
    //         for (Map.Entry<Long, Integer> entry : game.getScoreBoard().entrySet()) {
    //             Long userId = entry.getKey();
    //             Integer score = entry.getValue();
    //             userScoreMap.put(userId, userScoreMap.getOrDefault(userId, 0) + score);
    //         }
    //     }
    
    //     List<GameGetDTO> leaderboard = new ArrayList<>();
    //     for (Map.Entry<Long, Integer> entry : userScoreMap.entrySet()) {
    //         Long userId = entry.getKey();
    //         Integer totalScore = entry.getValue();
    
    //         User user = userRepository.findById(userId)
    //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
    //         GameGetDTO dto = new GameGetDTO();
    //         dto.setUserId(userId);
    //         dto.setUsername(user.getUsername());
    //         dto.setTotalScore(totalScore);
    //         leaderboard.add(dto);
    //     }
    
    //     leaderboard.sort((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()));
    
    //     return leaderboard;
    // }
}