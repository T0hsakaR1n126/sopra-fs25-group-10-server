package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreateResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameHintDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyJoinGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerAuthDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStartDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

/**
* DTOMapper
* This class is responsible for generating classes that will automatically
* transform/map the internal representation
* of an entity (e.g., the User) to the external/API representation (e.g.,
* UserGetDTO for getting, UserPostDTO for creating)
* and vice versa.
* Additional mappers can be defined for new entities.
* Always created one mapper for getting information (GET) and one mapper for
* creating information (POST).
*/
@Mapper
(componentModel = "spring", uses = {UserRepository.class})
public interface DTOMapper {
    
    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);
    
    //User mappings
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);
    
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "token", target = "token")
    UserGetDTO convertEntityToUserGetDTO(User user);
    
    // Game mappings
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "gameName", target = "gameName") 
    @Mapping(source = "maxPlayersNumber", target = "maxPlayersNumber")
    @Mapping(source = "time", target = "time") 
    @Mapping(source = "modeType", target = "modeType")
    @Mapping(source = "accessType", target = "accessType") 
    @Mapping(source = "password", target = "password")  
    Game convertGamePostDTOtoGameEntity(GamePostDTO gamePostDTO);
    
    @Mapping(source = "ownerId", target = "ownerId")
    //  @Mapping(target = "ownerName", expression = "java(userRepository.findByUserId(game.getOwnerId()).getUsername())")
    // @Mapping(source = "scoreBoard", target = "scoreBoard")
    @Mapping(source = "gameName", target = "gameName") 
    @Mapping(source = "maxPlayersNumber", target = "maxPlayersNumber")
    @Mapping(source = "currentPlayersNumber", target = "currentPlayersNumber")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "startTime", target = "starTime")
    @Mapping(source = "gameStatus", target = "gameStatus")
    @Mapping(source = "modeType", target = "modeType")
    @Mapping(source = "accessType", target = "accessType")
    @Mapping(source = "endTime", target = "endTime")
    // @Mapping(source = "finalScore", target = "finalScore")
    @Mapping(source = "gameCreationDate", target = "gameCreationDate")
    @Mapping(source = "maxHints", target = "maxHints")
    GameGetDTO convertGameEntityToGameGetDTO(Game game);
    
    
    //GameJoinPostDTO mapping
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "password", target = "password")
    Game mapGameJoinPostDTOToRequest(LobbyJoinGetDTO gameJoinDTO);
    
    // Profile mappings
    @Mapping(source = "username", target = "username")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "bio", target = "bio")
    User convertUserProfileDTOtoEntity(UserProfileDTO userProfileDTO);
    
    @Mapping(source = "playerId", target = "playerId")
    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "team.teamId", target = "teamId")
    @Mapping(source = "team.teamName", target = "teamName")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "game.gameId", target = "gameId")
    @Mapping(source = "score", target = "score")
    // @Mapping(source = "game.gameStatus", target = "gameStatus")
    // @Mapping(source = "playerStatus", target = "playerStatus")
    PlayerDTO convertPlayerToPlayerDTO(Player user);
    
    @Mapping(source = "playerId", target = "playerId")
    // @Mapping(source = "game.gameId", target = "gameId")
    @Mapping(source = "token", target = "token")
    GameStartDTO convertPlayerToGameStartDTO(Player player);
    
    @Mapping(source = "playerId", target = "playerId")
    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "team.teamId", target = "teamId")
    @Mapping(source = "team.teamName", target = "teamName")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "game.gameId", target = "gameId")
    @Mapping(source = "token", target = "token")
    // @Mapping(source = "playerStatus", target = "playerStatus")
    PlayerAuthDTO convertPlayerToPlayerAuthDTO(Player player);
    
    @Mapping(source = "game", target = "game")
    @Mapping(source = "player", target = "player")
    GameCreateResponseDTO convertGameToGameCreateResponseDTO(Game game, Player player);
    
    // @Mapping(source = "game.gameId", target = "Id")
    // @Mapping(source = "hints", target = "hints")
    // GameHintDTO convertGameHintDTOToGameHintDTO(Game game);
}
