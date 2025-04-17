package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
* Internal User Representation
* This class composes the internal representation of the user and defines how
* the user is stored in the database.
* Every variable will be mapped into a database field with the @Column
* annotation
* - nullable = false -> this cannot be left empty
* - unique = true -> this value must be unqiue across the database -> composes
* the primary key
*/
@Entity
@Table(name = "USER")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private UserStatus status;
    
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @Column
    private String avatar;
    
    @Column
    private String email;
    
    @Column
    private String bio;
    
    @ElementCollection
    @CollectionTable(name = "countryProgress", joinColumns = @JoinColumn(name = "userId"))
    @MapKeyColumn(name = "country")
    @Column(name = "correctGuesses")
    private Map<Long, Integer> countryProgress = new HashMap<>();
        
    @ManyToOne
    @JoinColumn(name = "gameId", nullable = true)
    private Game game;
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
}
