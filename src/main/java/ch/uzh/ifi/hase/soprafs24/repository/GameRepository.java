package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {
  Game findBygameName(String gameName);

  Game findBygameId(Long gameId);

  Game findByowner(String owner);
}
