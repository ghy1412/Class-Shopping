package com.dreamone.dao;

import com.dreamone.dataobject.PromoDO;
import com.dreamone.dataobject.PromoDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PromoDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PromoDO record);

    int insertSelective(PromoDO record);

    List<PromoDO> selectByExample(PromoDOExample example);

    PromoDO selectByPrimaryKey(Integer id);

    PromoDO selectByItemId(@Param("itemId") int itemId);

    int updateByPrimaryKeySelective(PromoDO record);

    int updateByPrimaryKey(PromoDO record);
}