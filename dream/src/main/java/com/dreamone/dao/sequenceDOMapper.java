package com.dreamone.dao;

import com.dreamone.dataobject.sequenceDO;
import com.dreamone.dataobject.sequenceDOExample;
import java.util.List;

public interface sequenceDOMapper {
    int deleteByPrimaryKey(String name);

    int insert(sequenceDO record);

    int insertSelective(sequenceDO record);

    List<sequenceDO> selectByExample(sequenceDOExample example);

    sequenceDO selectByPrimaryKey(String name);

    sequenceDO getSequenceByName(String name);

    int updateByPrimaryKeySelective(sequenceDO record);

    int updateByPrimaryKey(sequenceDO record);
}