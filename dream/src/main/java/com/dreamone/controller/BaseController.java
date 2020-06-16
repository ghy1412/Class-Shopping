package com.dreamone.controller;

import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class BaseController {
    public static final String CONTENT_TYPE_FORMER = "application/x-www-form-urlencoded";

/*    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        HashMap<String, Object> responseMap = new HashMap<>();
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException)ex;
            responseMap.put("errCode",businessException.getErrCode());
            responseMap.put("errMsg", businessException.getErrMsg());
        } else {
            responseMap.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
            responseMap.put("errMsg", EmBusinessError.UNKNOW_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseMap,"fail");
    }*/
}
