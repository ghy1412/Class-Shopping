package com.dreamone.service.impl;


import com.alibaba.druid.util.StringUtils;
import com.dreamone.dao.UserDOMapper;
import com.dreamone.dao.UserPasswordDOMapper;
import com.dreamone.dataobject.UserDO;
import com.dreamone.dataobject.UserPasswordDO;
import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.service.UserService;
import com.dreamone.service.model.UserModel;
import com.dreamone.validator.ValidationResult;
import com.dreamone.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValidatorImpl validator;
    @Override
    public void test() {
        UserDO userDO = new UserDO();
        userDO.setName("张三");
        userDO.setGender(false);
        userDO.setAge(11);
        userDO.setTelephone("3123");
        userDO.setRegisterMode("123");
        userDO.setThirdPartyId("213");
        System.out.println("开始");
        userDOMapper.insertSelective(userDO);
        System.out.println("结束");
    }

    @Override
    public UserModel getUserFromRedis(Integer key) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_valid"+key);
        if (userModel == null) {
            userModel = this.getUserbyId(key);
            redisTemplate.opsForValue().set("user_valid"+key,userModel);
            redisTemplate.expire("user_valid"+key,60, TimeUnit.SECONDS);
        }
        return userModel;
    }

    @Override
    public UserModel login(UserModel userModel) throws BusinessException {
        UserDO userDO = userDOMapper.selectByTelephone(userModel.getTelephone());
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_REGISTER_FAIL);
        }
        System.out.println("userDO "+userDO);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if (userPasswordDO == null) {
            throw new BusinessException(EmBusinessError.USER_REGISTER_FAIL);
        }
        System.out.println("password "+userPasswordDO);
        return convertUserModel(userDO, userPasswordDO);
    }

    @Override
    public UserModel getUserbyId(Integer id) {

        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertUserModel(userDO, userPasswordDO);

    }

    @Override
    public void register(UserModel userModel) throws BusinessException {
        System.out.println("开始注册");
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "参数不合法");
        }
/*        if (StringUtils.isEmpty(userModel.getName())
            || userModel.getAge() == null
            || StringUtils.isEmpty(userModel.getEncrptPassword())
            || StringUtils.isEmpty(userModel.getTelephone())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "参数不合法");
        }*/
        //userDo和 userPasswordDO
        ValidationResult validate = validator.validate(userModel);

        if (validate.hasErrors) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validate.getErrorMsg());
        }

        System.out.println("用户");
        UserDO userDO = converUserDOFormModel(userModel);

        int id = 0;
        try {
            id = userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(EmBusinessError.USER_REGISTER_FAIL, "手机号已注册");
        }
        System.out.println("id:" + id);
        System.out.println("密码注册");
        //passWord
        UserPasswordDO userPasswordDO = convertPasswordFormModel(userModel);
        if (userDO != null && userPasswordDO != null) {
            userPasswordDO.setUserId(userDO.getId());
        } else {
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        }
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    private UserPasswordDO convertPasswordFormModel(UserModel userModel) throws BusinessException {
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        if (userModel == null) {
            return null;
        }
        BeanUtils.copyProperties(userModel, userPasswordDO);
        return userPasswordDO;
    }
    private UserDO converUserDOFormModel(UserModel userModel) throws BusinessException {
        UserDO userDO = new UserDO();
        if (userModel == null) {
            return null;
        }
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserModel convertUserModel(UserDO userDO, UserPasswordDO userPasswordDO) {
        UserModel userModel = new UserModel();



        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        if (userDO != null) {
            BeanUtils.copyProperties(userDO, userModel);
            userModel.setId(userDO.getId());
        }

        return userModel;
    }

}
