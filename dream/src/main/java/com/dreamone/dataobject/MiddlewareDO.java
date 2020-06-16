package com.dreamone.dataobject;

import java.io.Serializable;

public class MiddlewareDO implements Serializable {
    private Integer id;

    private Integer noteId;

    private String answerName;

    private Integer answerNoteId;

    private Integer agree;

    private Integer notAgree;

    private String summary;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName == null ? null : answerName.trim();
    }

    public Integer getAnswerNoteId() {
        return answerNoteId;
    }

    public void setAnswerNoteId(Integer answerNoteId) {
        this.answerNoteId = answerNoteId;
    }

    public Integer getAgree() {
        return agree;
    }

    public void setAgree(Integer agree) {
        this.agree = agree;
    }

    public Integer getNotAgree() {
        return notAgree;
    }

    public void setNotAgree(Integer notAgree) {
        this.notAgree = notAgree;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }
}