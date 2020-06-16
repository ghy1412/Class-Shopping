package com.dreamone.dao;

import com.dreamone.dataobject.NoteDO;
import com.dreamone.dataobject.NoteDOExample;
import java.util.List;

public interface NoteDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NoteDO record);


    int insertSelective(NoteDO record);

    List<NoteDO> selectByExampleWithBLOBs(NoteDOExample example);

    List<NoteDO> selectByUserId(Integer userId, Integer left, Integer right);

    List<NoteDO> selectByExample(NoteDOExample example);

    NoteDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NoteDO record);

    int updateByPrimaryKeyWithBLOBs(NoteDO record);

    int updateByPrimaryKey(NoteDO record);
}