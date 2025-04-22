package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStartDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerAuthDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerSimpleDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerResultDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerAnswerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.HintGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.HintPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreateResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TeamVsTeamResultDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.OneVsOneResultDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.SoloResultDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.constant.Country;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    private Game game;
    private GamePostDTO gamePostDTO;
    private GameGetDTO gameGetDTO;

    @BeforeEach
    public void setup() {
        game = new Game();
        game.setGameId(1L);
        game.setGameName("Test Game");

        gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameName("Test Game");

        gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameName("Test Game");
    }

    @Test
    public void createGame_validInput_success() throws Exception {
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setGameId(1L);

        PlayerAuthDTO playerAuthDTO = new PlayerAuthDTO();
        playerAuthDTO.setPlayerId(100L);
        playerAuthDTO.setToken("mock-token");

        GameCreateResponseDTO responseDTO = new GameCreateResponseDTO();
        responseDTO.setGame(gameGetDTO);
        responseDTO.setPlayer(playerAuthDTO);

        given(gameService.createGame(any())).willReturn(responseDTO);

        MockHttpServletRequestBuilder request = post("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO));

        mockMvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.game.gameId").value(1))
            .andExpect(jsonPath("$.player.playerId").value(100))
            .andExpect(jsonPath("$.player.token").value("mock-token"));
    }

    @Test
    public void getGameLobby_success() throws Exception {
        given(gameService.getAllGames()).willReturn(List.of(game));

        mockMvc.perform(get("/lobby"))
                .andExpect(status().isOk());
    }

    @Test
    public void getGameReady_validId_success() throws Exception {
        given(gameService.getGameByGameId(1L)).willReturn(game);

        mockMvc.perform(get("/game/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void joinGame_validRequest_success() throws Exception {
        LobbyJoinPostDTO joinDTO = new LobbyJoinPostDTO();
        joinDTO.setUserId(1L);
        joinDTO.setPassword("pass");
    
        Player mockPlayer = new Player();
        mockPlayer.setPlayerId(1L);
        mockPlayer.setToken("token");
    
        given(gameService.userJoinGame(eq(1L), eq(1L), eq("pass"))).willReturn(mockPlayer);
    
        mockMvc.perform(post("/lobby/1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(joinDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.playerId").value(1))
            .andExpect(jsonPath("$.token").value("token"));
    }

    
    @Test
    public void exitGame_validRequest_success() throws Exception {
        mockMvc.perform(put("/lobbyOut/1"))
            .andExpect(status().isOk());
    }

    @Test
    public void startGame_validRequest_success() throws Exception {
        GameStartDTO startDTO = new GameStartDTO();
        startDTO.setPlayerId(1L);
        startDTO.setToken("token");

        mockMvc.perform(put("/start/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(startDTO)))
            .andExpect(status().isOk());
    }

    @Test
    public void playerForfeit_validRequest_success() throws Exception {
        PlayerSimpleDTO dto = new PlayerSimpleDTO();
        dto.setPlayerId(1L);
        dto.setToken("token");

        mockMvc.perform(put("/game/1/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getScoreboard_validRequest_success() throws Exception {
        given(gameService.getScoreBoard(1L)).willReturn(List.of(new PlayerDTO()));

        mockMvc.perform(get("/game/1/scoreBoard"))
            .andExpect(status().isOk());
    }  

    @Test
    public void getHint_validRequest_success() throws Exception {
        HintGetDTO hintDto = new HintGetDTO();
        hintDto.setHintText("Some Hint"); //lets mock the response here
    
        given(gameService.getHint(eq(1L), eq(1L), eq("token"), eq(1), eq(1L)))
            .willReturn(hintDto);
    
        mockMvc.perform(get("/game/1/1/hint/1")
                .param("playerId", "1")
                .param("token", "token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hintText").value("Some Hint"));
    }

    @Test
    public void answerProcessing_validRequest_success() throws Exception {
        PlayerAnswerDTO dto = new PlayerAnswerDTO();
        dto.setPlayerId(1L);
        dto.setToken("token");
        dto.setAnswer(Country.Germany);

        given(gameService.processingAnswer(eq(1L), eq(1L), eq(1L), eq("token"), eq(Country.Germany)))
            .willReturn(new PlayerResultDTO());

        mockMvc.perform(post("/game/1/1/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void skipQuestion_validRequest_success() throws Exception {
        PlayerSimpleDTO dto = new PlayerSimpleDTO();
        dto.setPlayerId(1L);
        dto.setToken("token");

        mockMvc.perform(post("/game/1/1/skip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void getGameResult_soloMode_success() throws Exception {
        SoloResultDTO resultDTO = new SoloResultDTO();

        PlayerDTO player = new PlayerDTO();
        player.setPlayerId(1L);
        resultDTO.setPlayer(player);
        resultDTO.setScoreboard(List.of(player));

        given(gameService.gameResult(1L)).willReturn(resultDTO);

        mockMvc.perform(get("/game/1/result")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getGameResult_oneVsOne_success() throws Exception {
        OneVsOneResultDTO resultDTO = new OneVsOneResultDTO();

        PlayerDTO winner = new PlayerDTO();
        winner.setPlayerId(1L);

        PlayerDTO loser = new PlayerDTO();
        loser.setPlayerId(2L);

        resultDTO.setWinner(winner);
        resultDTO.setLoser(loser);
        resultDTO.setScoreboard(List.of(winner, loser));
        resultDTO.setDraw(false);

        given(gameService.gameResult(1L)).willReturn(resultDTO);

        mockMvc.perform(get("/game/1/result")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getGameResult_teamVsTeam_success() throws Exception {
        TeamVsTeamResultDTO resultDTO = new TeamVsTeamResultDTO();

        resultDTO.setWinner("Team A");
        resultDTO.setLoser("Team B");
        resultDTO.setTeam1Score(150);
        resultDTO.setTeam2Score(120);
        resultDTO.setScoreboard(List.of(new PlayerDTO(), new PlayerDTO()));
        resultDTO.setDraw(false);

        given(gameService.gameResult(1L)).willReturn(resultDTO);

        mockMvc.perform(get("/game/1/result")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getGameReady_gameNotFound_shouldReturn404() throws Exception {
        given(gameService.getGameByGameId(99L))
            .willReturn(null);
    
        mockMvc.perform(get("/game/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void startGame_invalidToken_shouldReturn403() throws Exception {
        GameStartDTO startDTO = new GameStartDTO();
        startDTO.setPlayerId(1L);
        startDTO.setToken("invalid");
    
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"))
            .when(gameService).startGame(eq(1L), any(GameStartDTO.class));
    
        mockMvc.perform(put("/start/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(startDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void joinGame_wrongPassword_shouldReturn401() throws Exception {
        LobbyJoinPostDTO joinDTO = new LobbyJoinPostDTO();
        joinDTO.setUserId(1L);
        joinDTO.setPassword("wrong");
    
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password"))
            .when(gameService).userJoinGame(eq(1L), eq(1L), eq("wrong"));
    
        mockMvc.perform(post("/lobby/1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(joinDTO)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getHint_invalidRequest_shouldReturn403() throws Exception {
        HintPostDTO dto = new HintPostDTO();
        dto.setPlayerId(1L);
        dto.setToken("invalid");
    
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid hint order"))
            .when(gameService).getHint(eq(1L), eq(1L), eq("invalid"), eq(3), eq(1L));
    
        mockMvc.perform(post("/game/1/1/hint/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void answerProcessing_invalidCountry_shouldReturn400() throws Exception {
        PlayerAnswerDTO dto = new PlayerAnswerDTO();
        dto.setPlayerId(1L);
        dto.setToken("token");
        dto.setAnswer(null); // 模拟未传入合法国家
    
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid country"))
            .when(gameService).processingAnswer(eq(1L), eq(1L), eq(1L), eq("token"), isNull());
    
        mockMvc.perform(post("/game/1/1/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void exitGame_invalidPlayerId_shouldReturn404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"))
            .when(gameService).playerExitGame(999L);
    
        mockMvc.perform(put("/lobbyOut/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void playerForfeit_invalidToken_shouldReturn403() throws Exception {
        PlayerSimpleDTO dto = new PlayerSimpleDTO();
        dto.setPlayerId(1L);
        dto.setToken("invalid");
    
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"))
            .when(gameService).playerForfiet(eq(1L), eq(1L), eq("invalid"));
    
        mockMvc.perform(put("/game/1/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getScoreboard_gameNotFound_shouldReturn404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"))
            .when(gameService).getScoreBoard(404L);
    
        mockMvc.perform(get("/game/404/scoreBoard"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void skipQuestion_invalidToken_shouldReturn403() throws Exception {
        PlayerSimpleDTO dto = new PlayerSimpleDTO();
        dto.setPlayerId(1L);
        dto.setToken("wrong-token");
    
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"))
            .when(gameService).skipQuestion(eq(1L), eq(1L), eq("wrong-token"));
    
        mockMvc.perform(post("/game/1/1/skip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isForbidden());
    }
    
    // @Test
    // public void joinGame_success() throws Exception {
    //     doNothing().when(gameService).userJoinGame(any(), eq(1L));

    //     mockMvc.perform(put("/lobbyIn/1")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(gamePostDTO)))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // public void exitGame_success() throws Exception {
    //     doNothing().when(gameService).userExitGame(eq(1L));

    //     mockMvc.perform(put("/lobbyOut/1")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(gamePostDTO)))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // public void startGame_success() throws Exception {
    //     doNothing().when(gameService).startGame(1L);

    //     mockMvc.perform(put("/start/1"))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // public void submitScores_validInput_success() throws Exception {
    //     gamePostDTO.setScoreMap(Map.of(1L, 1800));
    //     gamePostDTO.setCorrectAnswersMap(Map.of(1L, 7));
    //     gamePostDTO.setTotalQuestionsMap(Map.of(1L, 10));

    //     doNothing().when(gameService).submitScores(eq(1L), any(), any(), any());

    //     mockMvc.perform(put("/games/1/end")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(gamePostDTO)))
    //             .andExpect(status().isNoContent());
    // }

    // @Test
    // public void getUserGameHistory_success() throws Exception {
    //     given(gameService.getGamesByUser(1L)).willReturn(List.of(gameGetDTO));

    //     mockMvc.perform(get("/users/1/history"))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // public void getLeaderboard_success() throws Exception {
    //     given(gameService.getLeaderboard()).willReturn(List.of(gameGetDTO));

    //     mockMvc.perform(get("/leaderboard"))
    //             .andExpect(status().isOk());
    // }

    // turn DTO to json
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
}