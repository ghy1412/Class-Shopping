package com.dreamone.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class ValidationResult {

    //校验结构是否有错
    public boolean hasErrors = false;

    //存放错误信息的map
    HashMap<String, String> errorMsgMap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public HashMap<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(HashMap<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    //定义一个取得所有错误的字符串
    public String getErrorMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }
}
