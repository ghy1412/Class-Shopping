package com.dreamone.dao;

import com.dreamone.dataobject.UserPasswordDO;
import com.dreamone.dataobject.UserPasswordDOExample;
import java.util.List;

public interface UserPasswordDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordDO record);

    int insertSelective(UserPasswordDO record);

    List<UserPasswordDO> selectByExample(UserPasswordDOExample example);

    UserPasswordDO selectByPrimaryKey(Integer id);

    UserPasswordDO selectByUserId(Integer id);

    int updateByPrimaryKeySelective(UserPasswordDO record);

    int updateByPrimaryKey(UserPasswordDO record);
}