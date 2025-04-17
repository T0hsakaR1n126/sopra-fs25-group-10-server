package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class HintEntry {
    private Long questionId;
    private String text;
    private Integer difficulty;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public HintEntry(){}

    public HintEntry(long questionId, String text, int difficulty) {
        this.questionId = questionId;
        this.text = text;
        this.difficulty = difficulty;
    }
}

