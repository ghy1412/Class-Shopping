package com.dreamone.controller;


import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.response.CommonReturnType;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.BindException;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception ex) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException)ex;
            responseMap.put("errCode",businessException.getErrCode());
            responseMap.put("errMsg", businessException.getErrMsg());
        } else {
            responseMap.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
            responseMap.put("errMsg", EmBusinessError.UNKNOW_ERROR.getErrMsg());
        }

        //ServletRequestBindingException
        //NoHandlerFoundException
        return CommonReturnType.create(responseMap,"fail");
    }
}
