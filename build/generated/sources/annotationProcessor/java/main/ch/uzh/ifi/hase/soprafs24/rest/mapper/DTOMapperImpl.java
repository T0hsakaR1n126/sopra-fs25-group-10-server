package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Team;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreateResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStartDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyJoinGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerAuthDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-22T13:10:18+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 17.0.13 (N/A)"
)
@Component
public class DTOMapperImpl implements DTOMapper {

    @Override
    public User convertUserPostDTOtoEntity(UserPostDTO userPostDTO) {
        if ( userPostDTO == null ) {
            return null;
        }

        User user = new User();

        user.setName( userPostDTO.getName() );
        user.setPassword( userPostDTO.getPassword() );
        user.setUsername( userPostDTO.getUsername() );
        user.setToken( userPostDTO.getToken() );
        user.setUserId( userPostDTO.getUserId() );

        return user;
    }

    @Override
    public UserGetDTO convertEntityToUserGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserGetDTO userGetDTO = new UserGetDTO();

        userGetDTO.setName( user.getName() );
        userGetDTO.setUserId( user.getUserId() );
        userGetDTO.setUsername( user.getUsername() );
        userGetDTO.setStatus( user.getStatus() );
        userGetDTO.setToken( user.getToken() );
        userGetDTO.setAvatar( user.getAvatar() );
        userGetDTO.setEmail( user.getEmail() );
        userGetDTO.setBio( user.getBio() );

        return userGetDTO;
    }

    @Override
    public Game convertGamePostDTOtoGameEntity(GamePostDTO gamePostDTO) {
        if ( gamePostDTO == null ) {
            return null;
        }

        Game game = new Game();

        game.setMaxPlayersNumber( gamePostDTO.getMaxPlayersNumber() );
        game.setAccessType( gamePostDTO.getAccessType() );
        game.setModeType( gamePostDTO.getModeType() );
        game.setPassword( gamePostDTO.getPassword() );
        game.setGameName( gamePostDTO.getGameName() );
        game.setTime( gamePostDTO.getTime() );
        game.setOwnerId( gamePostDTO.getOwnerId() );
        game.setUserId( gamePostDTO.getUserId() );

        return game;
    }

    @Override
    public GameGetDTO convertGameEntityToGameGetDTO(Game game) {
        if ( game == null ) {
            return null;
        }

        GameGetDTO gameGetDTO = new GameGetDTO();

        gameGetDTO.setGameId( game.getGameId() );
        gameGetDTO.setMaxHints( game.getMaxHints() );
        gameGetDTO.setGameStatus( game.getGameStatus() );
        gameGetDTO.setOwnerId( game.getOwnerId() );
        gameGetDTO.setMaxPlayersNumber( game.getMaxPlayersNumber() );
        gameGetDTO.setAccessType( game.getAccessType() );
        gameGetDTO.setStarTime( game.getStartTime() );
        gameGetDTO.setPassword( game.getPassword() );
        if ( game.getModeType() != null ) {
            gameGetDTO.setModeType( game.getModeType().name() );
        }
        gameGetDTO.setGameName( game.getGameName() );
        gameGetDTO.setCurrentPlayersNumber( game.getCurrentPlayersNumber() );
        gameGetDTO.setEndTime( game.getEndTime() );
        gameGetDTO.setGameCreationDate( game.getGameCreationDate() );
        gameGetDTO.setTime( game.getTime() );
        gameGetDTO.setStartTime( game.getStartTime() );

        return gameGetDTO;
    }

    @Override
    public Game mapGameJoinPostDTOToRequest(LobbyJoinGetDTO gameJoinDTO) {
        if ( gameJoinDTO == null ) {
            return null;
        }

        Game game = new Game();

        game.setUserId( gameJoinDTO.getUserId() );
        game.setPassword( gameJoinDTO.getPassword() );

        return game;
    }

    @Override
    public User convertUserProfileDTOtoEntity(UserProfileDTO userProfileDTO) {
        if ( userProfileDTO == null ) {
            return null;
        }

        User user = new User();

        user.setBio( userProfileDTO.getBio() );
        user.setAvatar( userProfileDTO.getAvatar() );
        user.setEmail( userProfileDTO.getEmail() );
        user.setUsername( userProfileDTO.getUsername() );

        return user;
    }

    @Override
    public PlayerDTO convertPlayerToPlayerDTO(Player user) {
        if ( user == null ) {
            return null;
        }

        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setTeamName( userTeamTeamName( user ) );
        playerDTO.setGameId( userGameGameId( user ) );
        playerDTO.setScore( user.getScore() );
        playerDTO.setPlayerName( user.getPlayerName() );
        playerDTO.setTeamId( userTeamTeamId( user ) );
        playerDTO.setUserId( userUserUserId( user ) );
        playerDTO.setPlayerId( user.getPlayerId() );
        playerDTO.setPlayerStatus( user.getPlayerStatus() );

        return playerDTO;
    }

    @Override
    public GameStartDTO convertPlayerToGameStartDTO(Player player) {
        if ( player == null ) {
            return null;
        }

        GameStartDTO gameStartDTO = new GameStartDTO();

        gameStartDTO.setPlayerId( player.getPlayerId() );
        gameStartDTO.setToken( player.getToken() );

        return gameStartDTO;
    }

    @Override
    public PlayerAuthDTO convertPlayerToPlayerAuthDTO(Player player) {
        if ( player == null ) {
            return null;
        }

        PlayerAuthDTO playerAuthDTO = new PlayerAuthDTO();

        playerAuthDTO.setTeamName( userTeamTeamName( player ) );
        Long gameId = userGameGameId( player );
        if ( gameId != null ) {
            playerAuthDTO.setGameId( String.valueOf( gameId ) );
        }
        playerAuthDTO.setPlayerName( player.getPlayerName() );
        playerAuthDTO.setTeamId( userTeamTeamId( player ) );
        playerAuthDTO.setUserId( userUserUserId( player ) );
        playerAuthDTO.setPlayerId( player.getPlayerId() );
        playerAuthDTO.setToken( player.getToken() );

        return playerAuthDTO;
    }

    @Override
    public GameCreateResponseDTO convertGameToGameCreateResponseDTO(Game game, Player player) {
        if ( game == null && player == null ) {
            return null;
        }

        GameCreateResponseDTO gameCreateResponseDTO = new GameCreateResponseDTO();

        if ( game != null ) {
            gameCreateResponseDTO.setGame( convertGameEntityToGameGetDTO( game ) );
        }
        if ( player != null ) {
            gameCreateResponseDTO.setPlayer( convertPlayerToPlayerAuthDTO( player ) );
        }

        return gameCreateResponseDTO;
    }

    private String userTeamTeamName(Player player) {
        if ( player == null ) {
            return null;
        }
        Team team = player.getTeam();
        if ( team == null ) {
            return null;
        }
        String teamName = team.getTeamName();
        if ( teamName == null ) {
            return null;
        }
        return teamName;
    }

    private Long userGameGameId(Player player) {
        if ( player == null ) {
            return null;
        }
        Game game = player.getGame();
        if ( game == null ) {
            return null;
        }
        Long gameId = game.getGameId();
        if ( gameId == null ) {
            return null;
        }
        return gameId;
    }

    private Long userTeamTeamId(Player player) {
        if ( player == null ) {
            return null;
        }
        Team team = player.getTeam();
        if ( team == null ) {
            return null;
        }
        Long teamId = team.getTeamId();
        if ( teamId == null ) {
            return null;
        }
        return teamId;
    }

    private Long userUserUserId(Player player) {
        if ( player == null ) {
            return null;
        }
        User user = player.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }
}
