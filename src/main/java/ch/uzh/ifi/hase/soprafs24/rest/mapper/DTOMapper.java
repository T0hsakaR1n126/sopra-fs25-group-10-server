package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Team;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    // User mappings
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

    // Game mappings (POST)
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "playersNumber", target = "playersNumber")
    @Mapping(source = "time", target = "time")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "players", target = "players")
    @Mapping(source = "teams", target = "teams")
    Game convertGamePostDTOtoGameEntity(GamePostDTO gamePostDTO);

    // Game mappings (GET)
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "playersNumber", target = "playersNumber")
    @Mapping(source = "realPlayersNumber", target = "realPlayersNumber")
    @Mapping(source = "scoreBoard", target = "scoreBoard")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "players", target = "players")
    @Mapping(source = "teams", target = "teams")
    GameGetDTO convertGameEntityToGameGetDTO(Game game);

    // Profile mappings
    @Mapping(source = "username", target = "username")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "bio", target = "bio")
    User convertUserProfileDTOtoEntity(UserProfileDTO userProfileDTO);

    // Extra mappers (you must define PlayerDTO and TeamDTO classes)
    List<PlayerGetDTO> playersToPlayerDTOs(List<Player> players);
    List<TeamGetDTO> teamsToTeamDTOs(List<Team> teams);

    List<Player> playerDTOsToPlayers(List<PlayerPostDTO> playerDTOs);
    List<Team> teamDTOsToTeams(List<TeamPostDTO> teamDTOs);
}
