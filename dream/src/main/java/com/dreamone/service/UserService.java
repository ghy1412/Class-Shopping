package com.dreamone.service;

import com.dreamone.error.BusinessException;
import com.dreamone.service.model.UserModel;

public interface UserService {
    UserModel getUserbyId(Integer id);

    void register(UserModel userModel) throws BusinessException;

    void test();

    UserModel getUserFromRedis(Integer key);

    UserModel login(UserModel userModel) throws BusinessException;
}
