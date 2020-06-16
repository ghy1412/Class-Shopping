package com.dreamone.service.model;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

public class NoteModel {
    //id
    private Integer id;
    //标题
    @NotNull(message = "标题不能为空")
    private String title;
    //分类
    @NotNull(message = "分类不能为空")
    private String category;

    //时间
    private String createTime;

    //文件
    private MultipartFile multipartFile;

    //文本
    @NotNull(message = "文本不能为空")
    private String body;
    //图片存储路劲
    private String imgUrl;
    //属于谁的
    private Integer userId;
    //回复的列表集合
    private List<MiddlewareModel> middleList;

    private Integer nfaId;

    public Integer getNfaId() {
        return nfaId;
    }

    public void setNfaId(Integer nfaId) {
        this.nfaId = nfaId;
    }


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

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
