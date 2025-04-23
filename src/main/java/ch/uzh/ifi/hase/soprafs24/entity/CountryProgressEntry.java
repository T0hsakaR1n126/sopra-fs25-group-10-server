package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import ch.uzh.ifi.hase.soprafs24.constant.Country;

@Embeddable
public class CountryProgressEntry {

    private Country country;
    private String infoLearnt;
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
    public String getInfoLearnt() {
        return infoLearnt;
    }
    public void setInfoLearnt(String infoLearnt) {
        this.infoLearnt = infoLearnt;
    }
}
