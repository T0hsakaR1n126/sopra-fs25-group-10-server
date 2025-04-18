package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.constant.Country;

public class UserLearningDTO {
    private Map<Country, CountryProgressInfo> progressMap = new HashMap<>();

    public Map<Country, CountryProgressInfo> getProgressMap() {
        return progressMap;
    }

    public void setProgressMap(Map<Country, CountryProgressInfo> progressMap) {
        this.progressMap = progressMap;
    }

    public static class CountryProgressInfo {
        private int correct;
        private List<String> infoLearnt;

        public int getCorrect() {
            return correct;
        }

        public void setCorrect(int correct) {
            this.correct = correct;
        }

        public List<String> getInfoLearnt() {
            return infoLearnt;
        }

        public void setInfoLearnt(List<String> infoLearnt) {
            this.infoLearnt = infoLearnt;
        }
    }
}
