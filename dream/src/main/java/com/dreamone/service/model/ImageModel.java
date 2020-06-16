package com.dreamone.service.model;

import javax.validation.constraints.NotNull;

public class ImageModel {
    private Integer id;

    @NotNull(message = "名字不能为空")
    private String name;
    @NotNull(message = "userId不能为空")
    private Integer userId;
    @NotNull(message = "message为空")
    private Integer matterId;
    @NotNull(message = "upload不能为空")
    private String loadTime;
    @NotNull(message = "保存路径不能为空")
    private String path;
    @NotNull(message = "MD5不能为空")
    private String md5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMatterId() {
        return matterId;
    }

    public void setMatterId(Integer matterId) {
        this.matterId = matterId;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
