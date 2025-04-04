package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public void changePassword(Long userId, String currentPassword, String newPassword) {
    // search user by ID
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // check if current password is correct
    if (!user.getPassword().equals(currentPassword)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect current password");
    }

    // Check if new password is valid
    if (newPassword == null || newPassword.trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must not be empty");
    }

    // update password
    user.setPassword(newPassword);
    userRepository.save(user);
  }

  public User login(User loginUser) {
    User userInDB = userRepository.findByUsername(loginUser.getUsername());
    if (userInDB == null || !userInDB.getPassword().equals(loginUser.getPassword())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    userInDB.setStatus(UserStatus.ONLINE);
    userInDB.setToken(UUID.randomUUID().toString());
    userRepository.save(userInDB);
    return userInDB;
  }

  public void logout(User user) {
    User userInDB = userRepository.findByToken(user.getToken());
    if (userInDB == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    userInDB.setStatus(UserStatus.OFFLINE);
    userRepository.save(userInDB);
  }

  public User userAuthenticate(User authenticateUser) {
    User userVerified = userRepository.findByToken(authenticateUser.getToken());
    if (userVerified == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Authenticated");
    }
    return userVerified;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }
  }
}
