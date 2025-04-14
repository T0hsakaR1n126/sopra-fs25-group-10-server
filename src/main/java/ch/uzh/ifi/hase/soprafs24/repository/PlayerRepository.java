package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByGameId(Long gameId);
    List<Player> findByTeamId(Long teamId);
    List<Player> findByPlayerStatus(PlayerStatus playerStatus);
    List<Player> findByGameIdAndPlayerStatus(Long gameId, PlayerStatus playerStatus);
    List<Player> findByGameIdAndPlayerStatusAndTeam_TeamId(Long gameId, PlayerStatus playerStatus, Long teamId);
}
