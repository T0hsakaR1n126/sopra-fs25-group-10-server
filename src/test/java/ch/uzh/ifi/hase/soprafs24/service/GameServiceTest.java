package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.*;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private GameService gameService;

    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game();
        game.setGameId(1L);
        game.setGameName("Test Game");
        game.setModeType(GameMode.SOLO);
        game.setGameStatus(GameStatus.WAITING);
        game.setPlayers(new ArrayList<>());
        game.setMaxHints(3);
    }

    @Test
    public void createGame_validInput_success() {
        // arrange
        game.setModeType(GameMode.SOLO);
        game.setMaxPlayersNumber(1);
    
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameName("Test Game");
    
        GameCreateResponseDTO expectedResponse = new GameCreateResponseDTO();
        expectedResponse.setGame(gameGetDTO);
    
        given(gameRepository.save(any(Game.class))).willReturn(game);
        given(playerRepository.save(any(Player.class))).willReturn(new Player());
    
        // act
        GameCreateResponseDTO responseDTO = gameService.createGame(game);
    
        // assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getGame());
        assertEquals("Test Game", responseDTO.getGame().getGameName());
    }

    @Test
    public void getGameByGameId_validId_success() {
        given(gameRepository.findBygameId(1L)).willReturn(game);
        Game found = gameService.getGameByGameId(1L);
        assertNotNull(found);
        assertEquals(1L, found.getGameId());
    }

    @Test
    public void test_getAllGames_validInputs_success() {
        given(gameRepository.findAll()).willReturn(List.of(game));
        List<Game> games = gameService.getAllGames();
        assertEquals(1, games.size());
    }

}
