package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByToken_success() {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("dummyPassword");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setAvatar("/avatar_1.png");
    user.setBio("");
    user.setEmail("");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByToken(user.getToken());

    // then
    assertNotNull(found.getUserId());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getStatus(), user.getStatus());
  }

  @Test
  public void findByToken_failed() {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("dummyPassword");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setAvatar("/avatar_1.png");
    user.setBio("");
    user.setEmail("");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByToken("2");

    // then
    assertNull(found);
  }
}
