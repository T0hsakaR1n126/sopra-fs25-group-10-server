package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Custom query to find a player by their userId (if needed)
    Optional<Player> findByUserId(Long userId);
    
    // Custom query to find a player by their playerId (if needed)
    Optional<Player> findByPlayerId(Long playerId);
}