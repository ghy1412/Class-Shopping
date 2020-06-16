package com.dreamone.dao;

import com.dreamone.dataobject.UserDO;
import com.dreamone.dataobject.UserDOExample;
import java.util.List;

public interface UserDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    List<UserDO> selectByExample(UserDOExample example);

    UserDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    UserDO selectByTelephone(String telephone);
}