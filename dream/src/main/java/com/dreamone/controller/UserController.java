package com.dreamone.controller;

import com.alibaba.druid.util.StringUtils;
import com.dreamone.controller.viewobject.UserVo;
import com.dreamone.error.BusinessException;
import com.dreamone.error.CommonError;
import com.dreamone.error.EmBusinessError;
import com.dreamone.response.CommonReturnType;
import com.dreamone.service.impl.UserServiceImpl;
import com.dreamone.service.model.UserModel;
import com.mysql.jdbc.util.Base64Decoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMER})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws NoSuchAlgorithmException, BusinessException {
        UserModel userModel = new UserModel();
        userModel.setTelephone(telephone);
        //userModel.setEncrptPassword(password);
        UserModel model = userService.login(userModel);
        System.out.println("登陆时候的User Id: "+model.getId());
        String encrptPassword = model.getEncrptPassword();
        if (password == null || encrptPassword == null || !encrptPassword.equals(EncodeByMd5(password))) {
            throw new BusinessException(EmBusinessError.USER_REGISTER_FAIL);
        }

        //将登陆凭证加入到用户成功登陆的session中
        //this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        //this.httpServletRequest.getSession().setAttribute("USER_LOGIN",model);

        //修改成若登陆成功, 将登陆信息和登录凭证存入redis
        String uuidToken = UUID.randomUUID().toString().replace("-","");
        System.out.println(uuidToken);
        redisTemplate.opsForValue().set(uuidToken,model);
        redisTemplate.expire(uuidToken, 3600, TimeUnit.SECONDS);
        return CommonReturnType.create(uuidToken);
    }


    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMER})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "name") String name,
                                             @RequestParam(name = "otpCode") String otpCode,
                                             @RequestParam(name = "gender") Integer gender,
                                             @RequestParam(name = "age") Integer age,
                                             @RequestParam(name = "telephone") String telephone,
                                             @RequestParam(name = "password") String password) throws BusinessException, NoSuchAlgorithmException {

        //首先验证验证码和otp是否正确
        System.out.println("register");
        System.out.println(telephone+"和"+otpCode);
        String inSessionCode = (String)this.httpServletRequest.getSession().getAttribute(telephone);
        System.out.println("sessionCode: "+inSessionCode);
        if (!StringUtils.equals(otpCode,inSessionCode)) {
            System.out.println("进入方法");
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "参数错误");
        }

        System.out.println(telephone+"和"+otpCode);
        UserModel userModel = new UserModel();
        userModel.setName(name);
        //先强转
        userModel.setTelephone(telephone);
        boolean gen = false;
        if (gender == 1) {
            gen = true;
        }
        userModel.setGender(gen);
        userModel.setAge(age);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(EncodeByMd5(password));
        userService.register(userModel);
        //userService.test();
        //调用注册业务逻辑
        System.out.println("调用业务逻辑");
        return CommonReturnType.create(null);
    }
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String encode = base64Encoder.encode(md5.digest(str.getBytes()));
        return encode;
    }

    @RequestMapping(value = "/getOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMER})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        //生成随机码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        int randomCode = 10000 + randomInt;
        //存储, 一般分布式应用采用redis, 现在采用session形式
        httpServletRequest.getSession().setAttribute(telephone, String.valueOf(randomCode));
        System.out.println("Session"+telephone+randomCode);

        //发送 //暂且不做
        System.out.println("telephone = " + telephone + "& randomCode = "+randomCode);
        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userbyId = userService.getUserbyId(id);
        if (userbyId == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        CommonReturnType commonReturnType = CommonReturnType.create(convertUserVo(userbyId));
        return commonReturnType;

    }

    private UserVo convertUserVo(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userModel, userVo);
        return userVo;
    }

}
