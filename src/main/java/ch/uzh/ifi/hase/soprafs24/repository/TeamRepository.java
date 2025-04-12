package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // Custom query to find teams by gameId (for getting teams in a particular game)
    List<Team> findByGame_GameId(Long gameId);

    // Optional method to find a team by its name (if needed)
    Optional<Team> findByTeamName(String teamName);

    // If you want to find teams by their status (e.g., for searching based on status)
    List<Team> findByTeamStatus(String teamStatus);
}
