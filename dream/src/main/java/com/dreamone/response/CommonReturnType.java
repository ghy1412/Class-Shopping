package com.dreamone.response;

public class CommonReturnType {
    //对应状态码, sucess 和 fail
    private String status;

    //status = success  是数据
    //status = fail 是通用的错误码格式
    private Object data;

    public static CommonReturnType create(Object result){
        return create(result, "success");
    }
    public static CommonReturnType create(Object result, String status){
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setData(result);
        commonReturnType.setStatus(status);
        return commonReturnType;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
