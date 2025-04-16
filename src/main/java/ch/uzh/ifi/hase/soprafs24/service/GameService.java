package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.constant.GameAccessType;
import ch.uzh.ifi.hase.soprafs24.constant.GameHints;
import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.GameRoleType;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.constant.TeamStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Team;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TeamRepository;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreateResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStartDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;
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
    private final TeamRepository teamRepository;
    private final DTOMapper dtoMapper = DTOMapper.INSTANCE;
    
    private static final String webSocketSlug = "/topic/games/";
    
    private Map<Country, List<Map<String, Object>>> generatedHintsA;
    private Map<Country, List<Map<String, Object>>> generatedHintsB;

    //timer
    private final Map<Long, Timer> runningTimers = new HashMap<>();
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    UtilService utilService = new UtilService();
    
    public GameService(
    @Qualifier("gameRepository") GameRepository gameRepository,
    @Qualifier("userRepository") UserRepository userRepository,
    @Qualifier("playerRepository") PlayerRepository playerRepository,
    @Qualifier("teamRepository") TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }
    
    public List<Game> getAllGames() {
        return this.gameRepository.findAll();
    }
    
    //checks
    public void checkIfOwnerExists(Long userId){
        User userwithUserId = userRepository.findByUserId(userId);
        if(userwithUserId == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner is not a user! Please register or login to create a game!");
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
    public GameCreateResponseDTO createGame(Game gameToCreate) {
        if (gameToCreate.getModeType() == null) {
            throw new IllegalArgumentException("Game mode must be specified.");
        }
        
        log.debug("Game MaxPlayersNumber: {}", gameToCreate.getMaxPlayersNumber());
        Integer maxPlayers = gameToCreate.getMaxPlayersNumber();
        
        if (gameToCreate.getModeType() == GameMode.SOLO) {
            if (maxPlayers == null) {
                gameToCreate.setMaxPlayersNumber(1); 
            } else if (maxPlayers != 1) {
                throw new IllegalArgumentException("For SOLO games, the number of players must be exactly 1.");
            }
            
            return createSoloGame(gameToCreate);
        } else {
            return createMultiplayerGame(gameToCreate);
        }
    }
    
    //SOLO game creation
    private GameCreateResponseDTO createSoloGame(Game gameToCreate) {
        // Create Game Object for SOLO game
        Game gameCreated = new Game();
        gameCreated.setModeType(gameToCreate.getModeType());
        gameCreated.setGameName(gameToCreate.getGameName());
        gameCreated.setTime(gameToCreate.getTime());
        gameCreated.setMaxPlayersNumber(1);  // Only one player in SOLO mode
        gameCreated.setCurrentPlayersNumber(1); // The first player is the owner
        gameCreated.setMaxHints(GameHints.MAX_HINTS);
        gameCreated.setAccessType(gameToCreate.getAccessType());
        gameCreated.setGameStatus(GameStatus.WAITING);
        gameCreated.setGameCreationDate(LocalDateTime.now());
        gameCreated.setScoreBoard(new HashMap<>());
        
        // Create the owner player (no teams for solo game)
        Long ownerId = gameToCreate.getOwnerId();
        Player ownerPlayer;
        if (ownerId != null) {
            User ownerUser = userRepository.findByUserId(ownerId);
            ownerPlayer = new Player(ownerUser, gameCreated);
        } else {
            ownerPlayer = new Player(null, gameCreated); // No user in solo mode
        }
        
        ownerPlayer.setPlayerStatus(PlayerStatus.WAITING);
        ownerPlayer.setToken(UUID.randomUUID().toString());
        ownerPlayer.setRole(GameRoleType.OWNER);
        
        // Save Game first
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        
        // Now that the game is saved, associate the player with the game
        ownerPlayer = playerRepository.save(ownerPlayer);
        gameCreated.addPlayer(ownerPlayer);
        gameCreated.setOwnerId(ownerId);
        
        // Save the final game and player
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        playerRepository.flush();
        
        log.debug("Created new SOLO Game: {}", gameCreated);
        return dtoMapper.convertGameToGameCreateResponseDTO(gameCreated, ownerPlayer);
    }
    
    private GameCreateResponseDTO createMultiplayerGame(Game gameToCreate) {
        Integer maxPlayers = gameToCreate.getMaxPlayersNumber();
        
        if (gameToCreate.getModeType() == GameMode.ONE_VS_ONE) {
            if (maxPlayers != null && maxPlayers != 2) {
                throw new IllegalArgumentException("For 1v1 games, the number of players must be exactly 2.");
            } else {
                gameToCreate.setMaxPlayersNumber(2);  // Ensure 2 players for 1v1
            }
        }
        else if (gameToCreate.getModeType() == GameMode.TEAM_VS_TEAM) {
            if (maxPlayers == null || maxPlayers <= 2 || maxPlayers % 2 != 0) {
                throw new IllegalArgumentException("For TEAM_VS_TEAM, max players must be an even number of at least 2.");
            }
        }
        
        Game gameCreated = new Game();
        gameCreated.setModeType(gameToCreate.getModeType());
        gameCreated.setGameName(gameToCreate.getGameName());
        gameCreated.setTime(gameToCreate.getTime());
        gameCreated.setMaxPlayersNumber(gameToCreate.getMaxPlayersNumber());
        gameCreated.setCurrentPlayersNumber(1); 
        gameCreated.setMaxHints(GameHints.MAX_HINTS);
        gameCreated.setAccessType(gameToCreate.getAccessType());
        gameCreated.setGameStatus(GameStatus.WAITING);
        gameCreated.setGameCreationDate(LocalDateTime.now());
        gameCreated.setScoreBoard(new HashMap<>());
        
        Long ownerId = gameToCreate.getOwnerId();
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID must be provided for non-SOLO games.");
        }
        checkIfOwnerExists(ownerId);
        checkIfGameHaveSameOwner(ownerId);
        
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();  // Ensure the game is saved before saving players
        
        List<Team> createdTeams = new ArrayList<>();
        if (gameToCreate.getModeType() == GameMode.TEAM_VS_TEAM) {
            int totalPlayers = gameToCreate.getMaxPlayersNumber();
            int desiredTeamSize = totalPlayers / 2;
            
            for (int i = 0; i < 2; i++) {
                Team team = new Team();
                team.setGame(gameCreated);
                team.setDesiredTeamSize(desiredTeamSize);
                team.setTeamStatus(TeamStatus.WAITING);
                teamRepository.save(team);
                createdTeams.add(team);
            }
            teamRepository.flush(); // Ensure teamIds are generated
        }
        
        User ownerUser = userRepository.findByUserId(ownerId);
        Player ownerPlayer = new Player(ownerUser, gameCreated);
        ownerPlayer.setPlayerStatus(PlayerStatus.WAITING);
        ownerPlayer.setToken(UUID.randomUUID().toString());
        ownerPlayer.setRole(GameRoleType.OWNER);
        
        if (gameToCreate.getModeType() == GameMode.TEAM_VS_TEAM && !createdTeams.isEmpty()) {
            Team firstTeam = createdTeams.get(0);
            ownerPlayer.setTeam(firstTeam);
            firstTeam.addPlayer(ownerPlayer);
        }
        
        ownerPlayer = playerRepository.save(ownerPlayer); 
        gameCreated.addPlayer(ownerPlayer); 
        gameCreated.setOwnerId(ownerId);
        
        if (gameToCreate.getAccessType() == GameAccessType.PRIVATE) {
            gameCreated.setPassword(gameToCreate.getPassword());
        }
        
        gameCreated = gameRepository.save(gameCreated);
        gameRepository.flush();
        playerRepository.flush();
        
        log.debug("Created new Multiplayer Game: {}", gameCreated);
        return dtoMapper.convertGameToGameCreateResponseDTO(gameCreated, ownerPlayer);
    }
    
    public Game getGameByGameId(Long gameId){
        return gameRepository.findBygameId(gameId);
    }
    
    public Player userJoinGame(Long gameId, Long userId, String password) {
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
        
        if (userId != null) {
            List<Player> existingPlayers = playerRepository.findByGame_GameIdAndUser_UserId(gameId, userId);
            if (existingPlayers != null && existingPlayers.stream().anyMatch(player -> player.getPlayerStatus() == PlayerStatus.WAITING)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in the game.");
            }
        }
        
        Player newPlayer;
        
        if (userId != null) {
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            
            newPlayer = new Player(user, targetGame);
            newPlayer.setPlayerStatus(PlayerStatus.WAITING);
            newPlayer.setToken(UUID.randomUUID().toString());
            
            newPlayer = playerRepository.save(newPlayer);
            playerRepository.flush();
            
            if (targetGame.getOwnerId() == null) {
                targetGame.setOwnerId(userId);
            }
        } else {
            newPlayer = new Player(null, targetGame);
            newPlayer.setPlayerStatus(PlayerStatus.WAITING);
            newPlayer.setToken(UUID.randomUUID().toString());
            playerRepository.save(newPlayer);
        }
        
        //TEAM VS TEAM - Assign player to a team randomly
        if (targetGame.getModeType() == GameMode.TEAM_VS_TEAM) {
            List<Team> teams = teamRepository.findByGame_GameId(targetGame.getGameId());
            
            if (teams.size() < 2) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Team setup is incorrect for team vs team game.");
            }
            
            Team team1 = teams.get(0);
            Team team2 = teams.get(1);
            
            long count1 = team1.getTeamSize();
            long count2 = team2.getTeamSize();
            
            if (count1 >= team1.getDesiredTeamSize() && count2 >= team2.getDesiredTeamSize()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both teams are full!");
            }
            
            Team selectedTeam;
            if (count1 < team1.getDesiredTeamSize() && count2 < team2.getDesiredTeamSize()) {
                selectedTeam = new Random().nextBoolean() ? team1 : team2;
            } else if (count1 < team1.getDesiredTeamSize()) {
                selectedTeam = team1;
            } else {
                selectedTeam = team2;
            }
            
            newPlayer.setTeam(selectedTeam);
            selectedTeam.addPlayer(newPlayer);
            teamRepository.save(selectedTeam);
        }
        
        targetGame.addPlayer(newPlayer);
        targetGame.setCurrentPlayersNumber(targetGame.getCurrentPlayersNumber() + 1);
        
        gameRepository.save(targetGame);
        gameRepository.flush();
        playerRepository.flush();
        
        List<Player> players = playerRepository.findByGame_GameId(targetGame.getGameId());
        List<PlayerDTO> playerDTOs = players.stream()
        .map(player -> dtoMapper.convertPlayerToPlayerDTO(player))
        .collect(Collectors.toList());
        
        messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", playerDTOs);
        
        log.info("Player {} joined game {}", newPlayer.getPlayerId(), targetGame.getGameId());
        
        if (targetGame.getCurrentPlayersNumber() == targetGame.getMaxPlayersNumber()) {
            messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/status",
            "Players full! Let's play!");
            log.info("Game {} is full. Ready to play!", targetGame.getGameId());
        }
        
        log.info("Player {} joined game {}", newPlayer.getPlayerId(), targetGame.getGameId());
        return newPlayer;
        
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
        
        targetGame.removePlayer(exitingPlayer);
        targetGame.setCurrentPlayersNumber(targetGame.getCurrentPlayersNumber() - 1);
        
        playerRepository.delete(exitingPlayer);
        playerRepository.flush();
        
        if (targetGame.getPlayers().isEmpty()) {
            gameRepository.delete(targetGame);
            gameRepository.flush();
            log.info("Game deleted due to no players remaining.");
            return;
        }
        
        if (isOwner) {
            Optional<Player> newOwner = targetGame.getPlayers().stream()
            .filter(p -> p.getUser() != null && p.getUser().getUserId() != null)
            .findFirst();
            
            if (newOwner.isPresent()) {
                targetGame.setOwnerId(newOwner.get().getUser().getUserId());
                log.info("Ownership transferred to player with userId {}", newOwner.get().getUser().getUserId());
            } else {
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
        List<PlayerDTO> playerDTOs = players.stream()
        .map(player -> dtoMapper.convertPlayerToPlayerDTO(player))
        .collect(Collectors.toList());
        messagingTemplate.convertAndSend("/topic/ready/" + targetGame.getGameId() + "/players", playerDTOs);
        log.info("A Player exited and was deleted. WebSocket update sent.");
    }
    
    public void startGame(Long gameId, GameStartDTO playerDTO) {
        Game gameToStart = gameRepository.findBygameId(gameId);
        
        if (gameToStart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        }

        if (gameToStart.getMaxPlayersNumber() != gameToStart.getCurrentPlayersNumber()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is not full yet.");
        }
        
        if (gameToStart.getGameStatus() != GameStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is not in a state to be started.");
        }
        
        Player player = playerRepository.findById(playerDTO.getPlayerId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        
        if (!player.getGame().getGameId().equals(gameId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Player does not belong to this game.");
        }
        
        if (!player.getRole().equals(GameRoleType.OWNER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Player is not the owner of the game.");
        }
        
        if (!playerDTO.getToken().equals(player.getToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token.");
        }
        
        gameToStart.setGameStatus(GameStatus.ACTIVE);
        LocalDateTime startTime = LocalDateTime.now();
        gameToStart.setStartTime(startTime);
        
        LocalDateTime endTime = startTime.plusSeconds(gameToStart.getTime() + 1);
        gameToStart.setEndTime(endTime);
        
        // Initialize scoreBoard (set the initial score for all players)
        Map<Long, Integer> scoreBoard = new HashMap<>();
        // for (Long playerId : gameToStart.getPlayers()) {
        //     scoreBoard.put(playerId, 0);  // Set initial score to 0
        // }
        // gameToStart.setScoreBoard(scoreBoard);
        
        // Map<Country, List<Map<String, Object>>> generatedHintsA = utilService.generateClues(gameToStart.getMaxHints());
        // Map<Country, List<Map<String, Object>>> generatedHintsB = utilService.generateClues(gameToStart.getMaxHints());
        
        // // Create GameGetDTO for sending the first clue to players
        // GameGetDTO gameHintDTO = new GameGetDTO();
        // gameHintDTO.setHints(generatedHintsA.values().iterator().next());  // For now, sending first clue of A
        // gameHintDTO.setTime(gameToStart.getTime());
        
        // // Save the game with the updated information (start time, end time, game status, etc.)
        gameRepository.save(gameToStart);
        startGameTimer(gameToStart);
        
        // // Send a WebSocket message to notify players that the game has started
        // messagingTemplate.convertAndSend("/topic/start/" + gameId + "/hints", gameHintDTO);
        // log.info("WebSocket sent start hints for gameId={}", gameId);
        
        // // Send a message saying that the game has started (general game start message)
        // messagingTemplate.convertAndSend("/topic/game/" + gameId, "Game ID " + gameId + " has started!");
    }
    
    private void startGameTimer(Game game) {
        Timer timer = new Timer(true);
        Long gameId = game.getGameId();
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                long secondsLeft = Duration.between(now, game.getEndTime()).getSeconds();
                
                if (secondsLeft <= 0) {
                    game.setGameStatus(GameStatus.FINISHED);
                    gameRepository.save(game);
                    messagingTemplate.convertAndSend("/topic/game/" + gameId + "/end", "Game Over!");
                    timer.cancel();
                } else {
                    messagingTemplate.convertAndSend("/topic/game/" + gameId + "/timer", secondsLeft);
                }
            }
        };
        
        timer.scheduleAtFixedRate(task, 0, 1000);
        runningTimers.put(gameId, timer);
    }
    
    public List<Player> getPlayersByGameId(Long gameId) {
        Game game = gameRepository.findBygameId(gameId);
    
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with ID " + gameId + " not found.");
        }
    
        List<Player> players = playerRepository.findByGame_GameId(gameId);
        if (players == null || players.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players found for game with ID" + gameId);
        }
        // Convert Player entities to PlayerDTOs
        return players;
    }
    
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