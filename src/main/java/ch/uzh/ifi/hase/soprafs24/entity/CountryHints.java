package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;


import ch.uzh.ifi.hase.soprafs24.constant.Country;
import javax.persistence.*;

@Entity
@Table(name = "COUNTRY_HINTS")
public class CountryHints implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @Column(nullable = true, length = 500)
    private String hintText;

    @Column(nullable = false)
    private int difficulty;

    // Constructors
    public CountryHints() {}

    public CountryHints(Country country, String hintText, int difficulty) {
        this.country = country;
        this.hintText = hintText;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
