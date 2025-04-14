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
            Integer playersNumber = gameToCreate.getPlayersNumber();
            if (playersNumber == null || playersNumber != 1) {
                throw new IllegalArgumentException("For SOLO games, the number of players must be exactly 1.");
            }
        } else if (gameToCreate.getModeType() == GameMode.ONE_VS_ONE) {
            if (gameToCreate.getPlayersNumber() != 1) {
                throw new IllegalArgumentException("For 1v1 games, the number of players must be exactly 2.");
            }
        }
        
        
        Game gameCreated = new Game();
        gameCreated.setModeType(gameToCreate.getModeType());
        gameCreated.setGameName(gameToCreate.getGameName());
        gameCreated.setTime(gameToCreate.getTime());
        gameCreated.setPlayersNumber(gameToCreate.getPlayersNumber());
        gameCreated.setRealPlayersNumber(1);
        gameCreated.setHintsNumber(GameHints.MAX_HINTS);
        gameCreated.setAccessType(gameToCreate.getAccessType());
        gameCreated.setGameStatus(GameStatus.WAITING);
        gameCreated.setGameCreationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")));
        gameCreated.setScoreBoard(new HashMap<>());
        
        Long ownerId = gameToCreate.getOwnerId();
        
        if (gameToCreate.getModeType() != GameMode.SOLO) {
            if (ownerId == null) {
                throw new IllegalArgumentException("Owner ID must be provided for non-solo games.");
            }
            checkIfOwnerExists(ownerId);
            checkIfGameHaveSameOwner(ownerId);
        }
        
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        
        User ownerUser = null;
        
        if (ownerId != null) {
            checkIfOwnerExists(ownerId);
            checkIfGameHaveSameOwner(ownerId);
            ownerUser = userRepository.findByUserId(ownerId);
        }
        
        Player ownerPlayer = new Player(ownerUser, gameCreated);
        ownerPlayer.setPlayerStatus(PlayerStatus.WAITING);
        ownerPlayer.assignPlayerName();
        
        playerRepository.save(ownerPlayer);
        gameCreated.addPlayer(ownerPlayer);
        gameCreated.setOwnerId(ownerId);
        
        log.debug("Added new player: {} to game: {}", ownerPlayer.getPlayerId(), gameCreated.getGameId());
        
        
        if (gameToCreate.getAccessType() == GameAccessType.PRIVATE) {
            gameCreated.setPassword(gameToCreate.getPassword());
        }
        
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        playerRepository.flush();
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
        
        // Check if the game is already full
        if (targetGame.getRealPlayersNumber() >= targetGame.getPlayersNumber()) {
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
            newPlayer.assignPlayerName();
            playerRepository.save(newPlayer);
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
        targetGame.setRealPlayersNumber(targetGame.getRealPlayersNumber() + 1);
        
        gameRepository.save(targetGame);
        gameRepository.flush();
        playerRepository.flush();
        
        // Notify via WebSocket
        List<Player> players = playerRepository.findByGameId(targetGame.getGameId());
        messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
        
        log.info("Player {} joined game {}", newPlayer.getPlayerId(), targetGame.getGameId());
    }
    
    // public void userExitGame(Long userId){
    //     Game targetGame = userRepository.findByUserId(userId).getGame();
    //     if(userId != targetGame.getOwnerId()){
    //         log.info("gameId: ", targetGame.getGameId());
    //         targetGame.removePlayer(userRepository.findByUserId(userId));
    //         targetGame.setRealPlayersNumber(targetGame.getRealPlayersNumber()-1);
    //         gameRepository.save(targetGame);
    //         gameRepository.flush();
    
    //         User targetUser = userRepository.findByUserId(userId);
    //         targetUser.setGame(null);
    //         userRepository.save(targetUser);
    //         userRepository.flush();
    
    //         List<User> players = getGamePlayers(targetGame.getGameId());
    //         messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
    //         log.info("websocket send!");
    //     }
    //     else if(targetGame.getRealPlayersNumber()==1){
    //         gameRepository.deleteByGameId(targetGame.getGameId());
    //         gameRepository.flush();
    
    //         User targetUser = userRepository.findByUserId(userId);
    //         targetUser.setGame(null);
    //         userRepository.save(targetUser);
    //         userRepository.flush();
    
    //         List<User> players = getGamePlayers(targetGame.getGameId());
    //         messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
    //         log.info("websocket send!");
    
    //     }
    //     else{
    //         targetGame.removePlayer(userRepository.findByUserId(userId));
    //         targetGame.setRealPlayersNumber(targetGame.getRealPlayersNumber()-1);
    //         targetGame.setOwnerId((targetGame.getPlayers()).get(0));
    //         gameRepository.save(targetGame);
    //         gameRepository.flush();
    
    //         User targetUser = userRepository.findByUserId(userId);
    //         targetUser.setGame(null);
    //         userRepository.save(targetUser);
    //         userRepository.flush();
    
    //         List<User> players = getGamePlayers(targetGame.getGameId());
    //         messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", players);
    //         log.info("websocket send!");
    //     }
    // }
    
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
    
    
    // public void startGame(Long gameId){
    //     Game gameToStart = gameRepository.findBygameId(gameId);
        
    //     //set time
    //     LocalDateTime now = LocalDateTime.now();    
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
    //     gameToStart.setGameCreationDate(now.format(formatter));
        
    //     //set scoreBoard
        
    //     (gameToStart.getScoreBoard()).put(gameToStart.getOwnerId(), 0);
    //     for (Long userId : gameToStart.getPlayers()) {
    //         User player = userRepository.findByUserId(userId);
    //         if (player == null) {
    //             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, (userRepository.findByUserId(userId)).getUsername() + " is not found");
    //         }
    //         (gameToStart.getScoreBoard()).put(userId, 0); 
    //     }
        
    //     generatedHintsA = utilService.generateClues(gameToStart.getHintsNumber());
    //     generatedHintsB = utilService.generateClues(gameToStart.getHintsNumber());
        
    //     GameGetDTO gameHintDTO = new GameGetDTO();
        
    //     gameHintDTO.setHints(generatedHintsA.values().iterator().next());
    //     gameHintDTO.setTime(gameToStart.getTime());
        
    //     messagingTemplate.convertAndSend("/topic/start/" + gameId + "/hints", gameHintDTO);
    //     log.info("websocket send!");
        
    // }
    
    // public GameGetDTO processingAnswer(GamePostDTO gamePostDTO, Long userId){
    
    //     //judge right or wrong and update game
    //     Game targetGame = gameRepository.findBygameId(gamePostDTO.getGameId());
    //     //sum up total questions
    //     Map<Long, Integer> totalQuestionsMap = targetGame.getTotalQuestionsMap();
    //     if (totalQuestionsMap.containsKey(userId)) {
    //         int totalQuestions = totalQuestionsMap.get(userId);
    //         totalQuestionsMap.put(userId, totalQuestions + 1);
    //         targetGame.setTotalQuestionsMap(totalQuestionsMap); 
    //     } else {
    //         totalQuestionsMap.put(userId, 1);
    //         targetGame.setTotalQuestionsMap(totalQuestionsMap); 
    //     }
    
    //     if(gamePostDTO.getSubmitAnswer() == generatedHintsA.keySet().iterator().next()){
    //         //sum up correct answers
    //         Map<Long, Integer> currentCorrectAnswersMap = targetGame.getCorrectAnswersMap();
    //         if (currentCorrectAnswersMap.containsKey(userId)) {
    //             int currentCorrect = currentCorrectAnswersMap.get(userId);
    //             currentCorrectAnswersMap.put(userId, currentCorrect + 1); 
    //             targetGame.setCorrectAnswersMap(currentCorrectAnswersMap);
    //         } else {
    //             currentCorrectAnswersMap.put(userId, 1);
    //             targetGame.setCorrectAnswersMap(currentCorrectAnswersMap);
    //         }
    //         //sum up total score
    //         Map<Long, Integer> scoreBoardMap = targetGame.getScoreBoard();
    //         int currentscore = scoreBoardMap.get(userId);
    //         scoreBoardMap.put(userId, currentscore + (100-(gamePostDTO.getHintUsingNumber()-1))); 
    //         targetGame.setScoreBoard(scoreBoardMap);
    //     } 
    //     gameRepository.save(targetGame);
    //     gameRepository.flush();
    
    //     generatedHintsA=generatedHintsB;
    //     generatedHintsB = utilService.generateClues(targetGame.getHintsNumber());
    
    //     GameGetDTO gameHintDTO = new GameGetDTO();
    
    //     gameHintDTO.setHints(generatedHintsA.values().iterator().next());
    //     return gameHintDTO; 
    
    // }
    
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
    //     if (game.getScoreBoard().size() >= game.getRealPlayersNumber()) {
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