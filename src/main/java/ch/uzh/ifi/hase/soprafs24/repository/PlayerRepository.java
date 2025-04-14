package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByGame_GameId(Long gameId);
    List<Player> findByTeam_TeamId(Long teamId);
    List<Player> findByPlayerStatus(PlayerStatus playerStatus);
    List<Player> findByGame_GameIdAndPlayerStatus(Long gameId, PlayerStatus playerStatus);
    List<Player> findByGame_GameIdAndPlayerStatusAndTeam_TeamId(Long gameId, PlayerStatus playerStatus, Long teamId);
}
