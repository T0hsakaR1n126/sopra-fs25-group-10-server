package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.time.LocalDate;
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

    @Autowired
    public GameService(
            @Qualifier("gameRepository") GameRepository gameRepository,
            @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public List<Game> getAllGames() {
      return this.gameRepository.findAll();
    }

    public Game createGame(Game gameToCreate) {
      Game gameCreated = new Game();

      List<String> players = new ArrayList<>();
      Map<String, Integer> scoreBoard = new HashMap<>();

      checkIfOwnerExists(gameToCreate.getOwner());
      gameCreated.setOwner(gameToCreate.getOwner());

      gameCreated.setScoreBoard(scoreBoard);
      gameCreated.setPlayers(players);
      gameCreated.setHintsNumber(5);
      checkIfGameExists(gameToCreate.getGameName());
      gameCreated.setGameName(gameToCreate.getGameName());
      gameCreated.setTime(gameToCreate.getTime());
      gameCreated.setPlayersNumber(gameToCreate.getPlayersNumber());
      gameCreated.setRealPlayersNumber(1);
      // gameCreated.setModeType(gameToCreate.getModeType());
      gameCreated.setPassword(gameToCreate.getPassword());
      gameCreated = gameRepository.save(gameCreated);
      gameRepository.flush();

      User owner = userRepository.findByUsername(gameToCreate.getOwner());
      checkIfOnwerHaveGame(owner.getUsername());
      owner.setGame(gameCreated);
      userRepository.save(owner);
      userRepository.flush();

      
      log.debug("Created new Game: {}", gameToCreate);
      return gameCreated;
    }

    public Game getGameByGameId(Long gameId){
      return gameRepository.findBygameId(gameId);
    }

    public void checkIfCanJoin(User userToJoin, Game gameToBeJoined, Long gameId){
      if(gameToBeJoined.getPassword().equals((gameRepository.findBygameId(gameId)).getPassword())){
        (gameRepository.findBygameId(gameId)).addPlayer(userToJoin);
        (gameRepository.findBygameId(gameId)).setRealPlayersNumber((gameRepository.findBygameId(gameId)).getRealPlayersNumber()+1);
      }
      else{
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Password! You can't join the game! Please try again!"); 
      }
    }

    public void checkIfOwnerExists(String username){
      User userwithUsername = userRepository.findByUsername(username);
      if(userwithUsername == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner doesn't exists! Please try an existed ownername");
      }
    }
    public void checkIfGameExists(String gameName){
      Game gameWithSameName = gameRepository.findBygameName(gameName);
      if(gameWithSameName != null){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GameName exists! Please try a new one!");
      }
    }
    public void checkIfOnwerHaveGame(String username){
      Game gameOfOwner = (userRepository.findByUsername(username)).getGame();
      if(gameOfOwner != null){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "This user have already create a game! Please use another one!");
      }
    }
}