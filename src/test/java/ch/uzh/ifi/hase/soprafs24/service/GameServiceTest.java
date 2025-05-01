package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UtilService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@ExtendWith(MockitoExtension.class)

public class GameServiceTest {
    @Autowired
    private UtilService utilService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Spy
    
    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private User owner;

    @BeforeEach
    public void setup() {
    
        testGame = new Game();
        testGame.setGameName("Test Game");
        testGame.setOwnerId(1L);
        testGame.setPlayersNumber(4);
        testGame.setTime(5);
        testGame.setModeType("solo");
        testGame.setPassword("1234");

        owner = new User();
        owner.setUserId(1L);

        when(userRepository.findByUserId(1L)).thenReturn(owner);
        when(gameRepository.findByownerId(1L)).thenReturn(null);
        when(gameRepository.findBygameName("Test Game")).thenReturn(null);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReflectionTestUtils.setField(gameService, "messagingTemplate", messagingTemplate);
    }

    @Test
    public void checkIfOwnerExists_ownerNotFound_throwsException() {
        when(userRepository.findByUserId(99L)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.checkIfOwnerExists(99L);
        });

        assertTrue(exception.getReason().toLowerCase().contains("owner"));
    }

    @Test
    public void checkIfGameHaveSameOwner_ownerHasGame_throwsException() {
        when(gameRepository.findByownerId(1L)).thenReturn(new Game());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.checkIfGameHaveSameOwner(1L);
        });

        assertTrue(exception.getReason().toLowerCase().contains("already create a game"));
    }

    @Test
    public void checkIfGameNameExists_duplicateName_throwsException() {
        when(gameRepository.findBygameName("Test Game")).thenReturn(new Game());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.checkIfGameNameExists("Test Game");
        });

        assertTrue(exception.getReason().toLowerCase().contains("gamename"));

    }

    @Test
    public void createGame_invalidModeType_throwsException() {
        testGame.setModeType("invalid-mode");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.createGame(testGame);
        });

        assertTrue(exception.getReason().toLowerCase().contains("invalid mode"));
    }

    @Test
    public void createGame_validInput_success() {
        Game result = gameService.createGame(testGame);

        assertNotNull(result);
        assertEquals("Test Game", result.getGameName());
        assertEquals(1L, result.getOwnerId());
        assertEquals(1, result.getRealPlayersNumber());
    }
    
    @Test
    public void userJoinGame_successfullyJoinsGame() {
        Long userId = 2L;

        Game mockGame = new Game();
        mockGame.setGameId(100L);
        mockGame.setPassword("pass123");
        mockGame.setGameRunning(false);
        mockGame.setPlayersNumber(4);
        mockGame.setRealPlayersNumber(1);
        mockGame.setPlayers(new ArrayList<>(List.of(1L)));

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("newPlayer");

        Game incoming = new Game();
        incoming.setGameId(100L);
        incoming.setPassword("pass123");

        // Mocks
        when(gameRepository.findBygameId(100L)).thenReturn(mockGame);
        when(userRepository.findByUserId(userId)).thenReturn(mockUser);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(messagingTemplate).convertAndSend(any(String.class), any(Object.class));
        doReturn(List.of(mockUser)).when(gameService).getGamePlayers(100L); // Mocks getGamePlayers

        // Act
        gameService.userJoinGame(incoming, userId);

        // Assert
        assertEquals(2, mockGame.getRealPlayersNumber());
        verify(gameRepository).save(any(Game.class));
        verify(userRepository).save(any(User.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/ready/100/players"), any(Object.class));
    }

    @Test
    public void userJoinGame_gameRunning_throwsUnauthorized() {
        Game runningGame = new Game();
        runningGame.setGameId(100L);
        runningGame.setGameRunning(true); // the game is running

        when(gameRepository.findBygameId(100L)).thenReturn(runningGame);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.userJoinGame(runningGame, 2L);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertTrue(exception.getReason().contains("game is running"));
    }

    @Test
    public void userJoinGame_gameFull_throwsBadRequest() {
        Game fullGame = new Game();
        fullGame.setGameId(100L);
        fullGame.setGameRunning(false);
        fullGame.setPlayersNumber(4);
        fullGame.setRealPlayersNumber(4); // Max players reached
    
        when(gameRepository.findBygameId(100L)).thenReturn(fullGame);
    
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.userJoinGame(fullGame, 2L);
        });
    
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getReason().contains("this game is full"));
    }
    
    @Test
    public void userJoinGame_wrongPassword_throwsUnauthorized() {
        Game dbGame = new Game();
        dbGame.setGameId(100L);
        dbGame.setGameRunning(false);
        dbGame.setPlayersNumber(4);
        dbGame.setRealPlayersNumber(2);
        dbGame.setPassword("correct123");

        Game joinRequest = new Game();
        joinRequest.setGameId(100L);
        joinRequest.setPassword("wrongPassword");

        when(gameRepository.findBygameId(100L)).thenReturn(dbGame);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.userJoinGame(joinRequest, 2L);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertTrue(exception.getReason().contains("Wrong Password"));
    }

    // @Test
    // public void userStartGame_successfullyStartGame() {
    //     Long gameId = 100L;
    //     Long ownerId = 1L;
    //     Long playerId = 2L;

    //     Game mockGame = new Game();
    //     mockGame.setGameId(gameId);
    //     mockGame.setOwnerId(ownerId);
    //     mockGame.setPlayers(new ArrayList<>(List.of(ownerId, playerId)));
    //     mockGame.setGameRunning(false);
    //     mockGame.setTime(5); // 5 minutes game duration

    //     User owner = new User();
    //     owner.setUserId(ownerId);
    //     owner.setUsername("ownerUser");
    //     User player = new User();
    //     player.setUserId(playerId);
    //     player.setUsername("playerUser");

    //     Map<Country, List<String>> generatedHints = new HashMap<>();
    //     Country country = Country.Afghanistan;
    //     generatedHints.put(country, List.of("Hint1", "Hint2","Hint3","Hint4","Hint5"));

    //     when(gameRepository.findBygameId(gameId)).thenReturn(mockGame);
    //     when(userRepository.findByUserId(ownerId)).thenReturn(owner);
    //     when(userRepository.findByUserId(playerId)).thenReturn(player);
    //     when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //     doNothing().when(messagingTemplate).convertAndSend(any(String.class), any(Object.class));
    //     doNothing().when(utilService).timingCounter(anyInt(), anyLong());

    //     gameService.startGame(gameId);

    //     assertTrue(mockGame.getGameRunning());
    //     assertEquals(0, mockGame.getScore(ownerId)); 
    //     assertEquals(0, mockGame.getScore(playerId));
    //     assertNotNull(mockGame.getGameCreationDate()); 

    //     verify(messagingTemplate).convertAndSend(eq("/topic/start/" + gameId + "/ready-time"), eq(5));
    //     verify(messagingTemplate).convertAndSend(eq("/topic/start/" + gameId + "/hints"), any(GameGetDTO.class));
    //     verify(utilService).timingCounter(eq(5 * 60), eq(gameId)); // 5 minutes converted to seconds
    //     verify(gameRepository, atLeastOnce()).save(mockGame);
    //     verify(gameRepository).flush();
    // }
}




    @Test
    public void getGamesByUser_success() {
        // Arrange
        Long userId = 1L;
        String username = "testuser";
    
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername(username);
    
        Game endedGame = new Game();
        endedGame.setEndTime(LocalDateTime.now());
        endedGame.setCorrectAnswersMap(Map.of(userId, 5));
        endedGame.setTotalQuestionsMap(Map.of(userId, 10));
        endedGame.setResultSummaryMap(Map.of(userId, "5 of 10 correct"));
        endedGame.setPlayers(new ArrayList<>(List.of(userId))); // 
    
        List<Game> gamesList = List.of(endedGame);
    
        // Mock repository
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(gameRepository.findByPlayersContaining(username)).thenReturn(gamesList);
    
        // Act
        List<GameGetDTO> result = gameService.getGamesByUser(userId);
    
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        GameGetDTO dto = result.get(0);
        assertEquals(5, dto.getCorrectAnswers());
        assertEquals(10, dto.getTotalQuestions());
        assertEquals("5 of 10 correct", dto.getResultSummary());
    
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findByPlayersContaining(username);
    }
    
    @Test
    public void getGamesByUser_userNotFound_throwsException() {
        Long userId = 1L;
    
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
    
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.getGamesByUser(userId);
        });
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void submitScores_gameNotFound_throwsException() {
        // Arrange
        Long gameId = 1L;
    
        when(gameRepository.findBygameId(gameId)).thenReturn(null);
    
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.submitScores(gameId, new HashMap<>(), new HashMap<>(), new HashMap<>());
        });
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("Game not found"));
    }
    
    @Test
    public void submitScores_validInput_updatesGameAndSaves() {
        // Arrange
        Long gameId = 1L;
        Long userId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setRealPlayersNumber(1);
        mockGame.setScoreBoard(new HashMap<>());
    
        Map<Long, Integer> scoreMap = Map.of(userId, 80);
        Map<Long, Integer> correctAnswersMap = Map.of(userId, 8);
        Map<Long, Integer> totalQuestionsMap = Map.of(userId, 10);
    
        when(gameRepository.findBygameId(gameId)).thenReturn(mockGame);
    
        // Act
        gameService.submitScores(gameId, scoreMap, correctAnswersMap, totalQuestionsMap);
    
        // Assert
        assertEquals(80, mockGame.getScoreBoard().get(userId));
        assertEquals(8, mockGame.getCorrectAnswersMap().get(userId));
        assertEquals(10, mockGame.getTotalQuestionsMap().get(userId));
        assertEquals("8 of 10 correct", mockGame.getResultSummaryMap().get(userId));
        assertNotNull(mockGame.getEndTime());  // 游戏应该结束
        verify(gameRepository, times(1)).save(mockGame);
    }

    @Test
    public void submitScores_partialSubmission_noEndTime() {
        // Arrange
        Long gameId = 2L;
        Long userId = 200L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setRealPlayersNumber(3); // 总共需要3个人才算完成
        mockGame.setScoreBoard(new HashMap<>()); // 当前得分人数不够
    
        Map<Long, Integer> scoreMap = Map.of(userId, 50); // 只提交了一个人
        Map<Long, Integer> correctAnswersMap = Map.of(userId, 5);
        Map<Long, Integer> totalQuestionsMap = Map.of(userId, 10);
    
        when(gameRepository.findBygameId(gameId)).thenReturn(mockGame);
    
        // Act
        gameService.submitScores(gameId, scoreMap, correctAnswersMap, totalQuestionsMap);
    
        // Assert
        assertEquals(50, mockGame.getScoreBoard().get(userId));
        assertEquals(5, mockGame.getCorrectAnswersMap().get(userId));
        assertEquals(10, mockGame.getTotalQuestionsMap().get(userId));
        assertEquals("5 of 10 correct", mockGame.getResultSummaryMap().get(userId));
        assertNull(mockGame.getEndTime());  // 游戏**不应该结束**
        verify(gameRepository, times(1)).save(mockGame);
    }


    @Test
    public void getLeaderboard_success_returnsSortedLeaderboard() {
        // Arrange
        Game finishedGame1 = new Game();
        finishedGame1.setEndTime(LocalDateTime.now());
        finishedGame1.setScoreBoard(Map.of(1L, 50, 2L, 80)); // 两个玩家
    
        Game finishedGame2 = new Game();
        finishedGame2.setEndTime(LocalDateTime.now());
        finishedGame2.setScoreBoard(Map.of(1L, 100, 2L, 20)); // 第二局比赛
    
        when(gameRepository.findAll()).thenReturn(List.of(finishedGame1, finishedGame2));
    
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("Alice");
    
        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("Bob");
    
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
    
        // Act
        List<GameGetDTO> leaderboard = gameService.getLeaderboard();
    
        // Assert
        assertEquals(2, leaderboard.size());
        assertEquals("Alice", leaderboard.get(0).getUsername()); // Alice总分150，排第一
        assertEquals("Bob", leaderboard.get(1).getUsername());   // Bob总分100，排第二
    }

    @Test
    public void getLeaderboard_gameWithoutEndTime_isIgnored() {
        // Arrange
        Game unfinishedGame = new Game(); // 没有设置endTime
        unfinishedGame.setScoreBoard(Map.of(1L, 50));
    
        when(gameRepository.findAll()).thenReturn(List.of(unfinishedGame));
    
        // Act
        List<GameGetDTO> leaderboard = gameService.getLeaderboard();
    
        // Assert
        assertTrue(leaderboard.isEmpty()); // 没有完成的game，不应该算进排行榜
    }

    @Test
    public void getLeaderboard_userNotFound_throwsException() {
        // Arrange
        Game finishedGame = new Game();
        finishedGame.setEndTime(LocalDateTime.now());
        finishedGame.setScoreBoard(Map.of(1L, 100)); // 只有一个玩家
    
        when(gameRepository.findAll()).thenReturn(List.of(finishedGame));
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); // 故意查不到用户
    
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.getLeaderboard();
        });
    
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void userExitGame_userIsNotOwner_success() {
        Long ownerId = 1L;
        Long userId = 2L;
        Long gameId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setOwnerId(ownerId);
        mockGame.setPlayers(new ArrayList<>(List.of(ownerId, userId)));
        mockGame.setRealPlayersNumber(2);
    
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setGame(mockGame);
    
        when(userRepository.findByUserId(userId)).thenReturn(mockUser);
        doReturn(List.of()).when(gameService).getGamePlayers(gameId); // 🛠加上这一行，mock getGamePlayers避免空指针！
    
        // Act
        gameService.userExitGame(userId);
    
        // Assert
        verify(gameRepository).save(mockGame);
        verify(userRepository).save(mockUser);
        assertEquals(1, mockGame.getRealPlayersNumber());
        assertNull(mockUser.getGame());
    }
    

    @Test
    public void userExitGame_ownerIsOnlyPlayer_deletesGame() {
        Long ownerId = 1L;
        Long gameId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setOwnerId(ownerId);
        mockGame.setPlayers(new ArrayList<>(List.of(ownerId)));
        mockGame.setRealPlayersNumber(1);
    
        User mockUser = new User();
        mockUser.setUserId(ownerId);
        mockUser.setGame(mockGame);
    
        when(userRepository.findByUserId(ownerId)).thenReturn(mockUser);
    
        // Act
        gameService.userExitGame(ownerId);
    
        // Assert
        verify(gameRepository).deleteByGameId(gameId);
        verify(userRepository).save(mockUser);
        assertNull(mockUser.getGame());
    }

    @Test
    public void userExitGame_ownerWithOtherPlayers_transfersOwnership() {
        Long ownerId = 1L;
        Long newOwnerId = 2L;
        Long gameId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setOwnerId(ownerId);
        mockGame.setRealPlayersNumber(2);
        mockGame.setPlayers(new ArrayList<>(List.of(ownerId, newOwnerId))); // 🛠 很重要，不然players是null
    
        User ownerUser = new User();
        ownerUser.setUserId(ownerId);
        ownerUser.setGame(mockGame);
    
        User newOwnerUser = new User();
        newOwnerUser.setUserId(newOwnerId);
        newOwnerUser.setGame(mockGame);
    
        when(userRepository.findByUserId(ownerId)).thenReturn(ownerUser);
        when(userRepository.findByUserId(newOwnerId)).thenReturn(newOwnerUser);
    
        doReturn(new ArrayList<>(List.of(newOwnerUser))).when(gameService).getGamePlayers(gameId); // mock getGamePlayers避免NPE
    
        // Act
        gameService.userExitGame(ownerId);
    
        // Assert
        verify(gameRepository).save(mockGame);
        verify(userRepository).save(ownerUser);
        assertEquals(newOwnerId, mockGame.getOwnerId()); // 新的Owner应该是newOwnerId
        assertNull(ownerUser.getGame());
    }   



    // @Test
    // public void getHintsOfOneCountry_cacheEnough_noRefill() {
    //     // Arrange
    //     Map<Country, List<Map<String, Object>>> expectedHint = Map.of(
    //         new Country("Testland"), List.of(Map.of("hint", "test hint"))
    //     );
        
    //     Queue<Map<Country, List<Map<String, Object>>>> hintQueue = new LinkedList<>();
    //     hintQueue.add(expectedHint);
        
    //     for (int i = 0; i < 25; i++) {
    //         hintQueue.add(Map.of(new Country("Other" + i), List.of()));
    //     }
        
    //     when(utilService.getHintCache()).thenReturn(hintQueue);
    
    //     // Act
    //     Map<Country, List<Map<String, Object>>> result = yourService.getHintsOfOneCountry();
    
    //     // Assert
    //     assertEquals(expectedHint, result);
    //     verify(utilService, never()).refillAsync(); // 不应该补充缓存
    // }
    



    @Test
    public void giveupGame_singlePlayer_gameDeleted() {
        Long userId = 1L;
        Long gameId = 100L;

        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setPlayers(new ArrayList<>(List.of(userId)));
        mockGame.setScoreBoard(new HashMap<>());
        mockGame.setRealPlayersNumber(1);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setGame(mockGame);

        when(userRepository.findByUserId(userId)).thenReturn(mockUser);

        // Act
        gameService.giveupGame(userId);

        // Assert
        verify(gameRepository).deleteByGameId(gameId);
    }

    @Test
    public void giveupGame_multiplePlayers_userIsOwner_transfersOwnership() {
        Long ownerId = 1L;
        Long otherId = 2L;
        Long gameId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setOwnerId(ownerId);
        mockGame.setPlayers(new ArrayList<>(List.of(ownerId, otherId)));
        mockGame.setScoreBoard(new HashMap<>(Map.of(ownerId, 10, otherId, 20)));
        mockGame.setRealPlayersNumber(2);
    
        User ownerUser = new User();
        ownerUser.setUserId(ownerId);
        ownerUser.setGame(mockGame);
    
        User otherUser = new User();
        otherUser.setUserId(otherId);
        otherUser.setGame(mockGame);
    
        when(userRepository.findByUserId(ownerId)).thenReturn(ownerUser);
        when(userRepository.findByUserId(otherId)).thenReturn(otherUser);
        doReturn(List.of(otherUser)).when(gameService).getGamePlayers(gameId);
    
        // Act
        gameService.giveupGame(ownerId);
    
        // Assert
        verify(gameRepository).save(mockGame);
        assertEquals(otherId, mockGame.getOwnerId());
        assertEquals(1, mockGame.getRealPlayersNumber());
    }

    @Test
    public void giveupGame_multiplePlayers_userIsNotOwner() {
        Long ownerId = 1L;
        Long quittingId = 2L;
        Long gameId = 100L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setOwnerId(ownerId);
        mockGame.setPlayers(new ArrayList<>(List.of(ownerId, quittingId)));
        mockGame.setScoreBoard(new HashMap<>(Map.of(ownerId, 15, quittingId, 10)));
        mockGame.setRealPlayersNumber(2);
    
        User quittingUser = new User();
        quittingUser.setUserId(quittingId);
        quittingUser.setGame(mockGame);
    
        User ownerUser = new User();
        ownerUser.setUserId(ownerId);
        ownerUser.setGame(mockGame);
    
        when(userRepository.findByUserId(quittingId)).thenReturn(quittingUser);
        when(userRepository.findByUserId(ownerId)).thenReturn(ownerUser);
        doReturn(List.of(ownerUser)).when(gameService).getGamePlayers(gameId);
    
        // Act
        gameService.giveupGame(quittingId);
    
        // Assert
        verify(gameRepository).save(mockGame);
        assertEquals(1, mockGame.getRealPlayersNumber());
        assertEquals(ownerId, mockGame.getOwnerId());  // owner 没变
    }

    @Test
    public void saveGame_gameNotFound_nothingHappens() {
        // Arrange
        Long gameId = 1L;
        when(gameRepository.findBygameId(gameId)).thenReturn(null);
    
        // Act
        gameService.saveGame(gameId);
    
        // Assert
        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).deleteByGameId(any());
    }

    @Test
    public void saveGame_gameExists_playersSavedAndGameDeleted() {
        // Arrange
        Long gameId = 1L;
        Long userId = 100L;

        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setGameName("Mock Game");
        mockGame.setPlayers(List.of(userId));
        mockGame.updateScore(userId, 80);
        mockGame.updateCorrectAnswers(userId, 8);
        mockGame.updateTotalQuestions(userId, 10);

        User mockUser = new User();
        mockUser.setUserId(userId);

        when(gameRepository.findBygameId(gameId)).thenReturn(mockGame);
        when(userRepository.findByUserId(userId)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        gameService.saveGame(gameId);

        // Assert
        verify(userRepository).save(mockUser);
        verify(userRepository, times(1)).flush();
        verify(gameRepository).deleteByGameId(gameId);

        // 进一步验证mockUser内容
        assertNull(mockUser.getGame());
        assertEquals(BigDecimal.valueOf(0), mockUser.getLevel());
    }
    
    @Test
    public void getGamePlayers_success_multiplePlayers() {
        // Arrange
        Long gameId = 1L;
        Long userId1 = 100L;
        Long userId2 = 200L;
    
        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setPlayers(List.of(userId1, userId2));
        mockGame.setPlayersNumber(2);
    
        User user1 = new User();
        user1.setUserId(userId1);
        user1.setUsername("Player1");
    
        User user2 = new User();
        user2.setUserId(userId2);
        user2.setUsername("Player2");
    
        when(gameRepository.findBygameId(gameId)).thenReturn(mockGame);
        when(userRepository.findByUserId(userId1)).thenReturn(user1);
        when(userRepository.findByUserId(userId2)).thenReturn(user2);
    
        doNothing().when(messagingTemplate).convertAndSend(anyString(), ArgumentMatchers.<Object>any());
    
        // Act
        List<User> result = gameService.getGamePlayers(gameId);
    
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        verify(messagingTemplate).convertAndSend(eq("/topic/playersNumber"), eq(2));
    }
    
    

}