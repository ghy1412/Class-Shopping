package com.dreamone.service.model;

public class MiddlewareModel {
    //id
    private Integer id;
    //问题的id
    private Integer noteId;
    //回答的id
    private Integer answerNoteId;
    //点赞数量
    private Integer agree;
    //踩的数量
    private Integer notAgree;
    //回答问题的姓名. 方便展示
    private String answerName;

    //摘要
    private String Summary;

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public Integer getAnswerNoteId() {
        return answerNoteId;
    }

    public void setAnswerNoteId(Integer answerNoteId) {
        this.answerNoteId = answerNoteId;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

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
}
