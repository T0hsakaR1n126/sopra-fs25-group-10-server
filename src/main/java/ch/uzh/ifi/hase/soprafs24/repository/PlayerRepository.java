package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByPlayerId(Long playerId);
    List<Player> findByGame_GameId(Long gameId);  // corrected
    List<Player> findByTeam_TeamId(Long teamId);
    List<Player> findByPlayerStatus(PlayerStatus playerStatus);
    List<Player> findByGame_GameIdAndPlayerStatus(Long gameId, PlayerStatus playerStatus);
    List<Player> findByGame_GameIdAndPlayerStatusAndTeam_TeamId(Long gameId, PlayerStatus playerStatus, Long teamId);
    List<Player> findByGame_GameIdAndUser_UserId(@Param("gameId") Long gameId, @Param("userId") Long userId);

    // query for getting players at once
    @Query("SELECT p FROM Player p WHERE p.game.gameId = :gameId AND p.playerStatus = :status")
    List<Player> findPlayersInGameWithStatus(@Param("gameId") Long gameId, @Param("status") PlayerStatus status);
}
