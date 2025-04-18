package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLearningDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
* User Controller
* This class is responsible for handling all REST request that are related to
* the user.
* The controller will receive the request and delegate the execution to the
* UserService and finally return the result.
*/
@RestController
public class UserController {

    private final GameService gameService;
    
    private final UserService userService;
    
    UserController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }
    
    
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        
        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }
    
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
        
    }
    
    
    @PutMapping("/users/pwd")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody UserPasswordDTO userPasswordDTO) {
        // verification
        User userInput = new User();
        userInput.setToken(userPasswordDTO.getToken()); //why is this necessary?
        User authenticatedUser = userService.userAuthenticate(userInput);
        
        // change password
        userService.changePassword(authenticatedUser.getUserId(), userPasswordDTO.getCurrentPassword(), userPasswordDTO.getNewPassword());
    }
    
    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO userAuthenticate(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        
        User userVerified = userService.userAuthenticate(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userVerified);
    }
    
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO login(@RequestBody UserPostDTO userPostDTO) {
        User loginInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User loggedInUser = userService.login(loginInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
    }
    
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody UserPostDTO userPostDTO) {
        // verification
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User authenticatedUser = userService.userAuthenticate(userInput);
        
        userService.logout(authenticatedUser);
    }
    
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserProfile(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
    
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO updateUserProfile(@PathVariable("userId") Long userId,@RequestBody UserProfileDTO userProfileDTO) {
        User updatedInfo = DTOMapper.INSTANCE.convertUserProfileDTOtoEntity(userProfileDTO);
        User updatedUser = userService.updateUserProfile(userId, updatedInfo);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
    }
    
    
    @GetMapping("/users/{userId}/learning")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLearningDTO getUserProgress(@PathVariable("userId") Long userId) {
        // todo: add token verification
        return userService.getUserLearning(userId);
    }

    @GetMapping("/users/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerDTO> getUserHistory(@PathVariable("userId") Long userId) {
        // todo: add token verification
        return gameService.getUserHistory(userId);
    }
}
