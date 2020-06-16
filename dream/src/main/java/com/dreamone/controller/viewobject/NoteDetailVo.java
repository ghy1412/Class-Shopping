package com.dreamone.controller.viewobject;

import com.dreamone.service.model.MiddlewareModel;

import java.util.List;

public class NoteDetailVo {
    private Integer id;
    //标题
    private String title;
    //分类
    private String category;
    //时间
    private String createTime;
    //文本
    private String body;
    //图片存储路劲
    private String imgUrl;
    //属于谁的
    private Integer userId;

    private List<MiddlewareModel> middleList;

    public List<MiddlewareModel> getMiddleList() {
        return middleList;
    }

    public void setMiddleList(List<MiddlewareModel> middleList) {
        this.middleList = middleList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
