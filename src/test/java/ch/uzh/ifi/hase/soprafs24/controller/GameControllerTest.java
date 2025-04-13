package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        given(gameService.createGame(any())).willReturn(game);

        MockHttpServletRequestBuilder request = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
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
    public void joinGame_success() throws Exception {
        doNothing().when(gameService).userJoinGame(any(), eq(1L));

        mockMvc.perform(put("/lobbyIn/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void exitGame_success() throws Exception {
        doNothing().when(gameService).userExitGame(eq(1L));

        mockMvc.perform(put("/lobbyOut/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void startGame_success() throws Exception {
        doNothing().when(gameService).startGame(1L);

        mockMvc.perform(put("/start/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void submitScores_validInput_success() throws Exception {
        gamePostDTO.setScoreMap(Map.of(1L, 1800));
        gamePostDTO.setCorrectAnswersMap(Map.of(1L, 7));
        gamePostDTO.setTotalQuestionsMap(Map.of(1L, 10));

        doNothing().when(gameService).submitScores(eq(1L), any(), any(), any());

        mockMvc.perform(put("/games/1/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserGameHistory_success() throws Exception {
        given(gameService.getGamesByUser(1L)).willReturn(List.of(gameGetDTO));

        mockMvc.perform(get("/users/1/history"))
                .andExpect(status().isOk());
    }

    @Test
    public void getLeaderboard_success() throws Exception {
        given(gameService.getLeaderboard()).willReturn(List.of(gameGetDTO));

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().isOk());
    }

    // 辅助方法：将 DTO 转换为 JSON 字符串
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
}