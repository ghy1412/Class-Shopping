package com.dreamone.dao;

import com.dreamone.dataobject.MiddlewareDO;
import com.dreamone.dataobject.MiddlewareDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MiddlewareDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MiddlewareDO record);

    int insertSelective(MiddlewareDO record);

    List<MiddlewareDO> selectByExample(MiddlewareDOExample example);

    MiddlewareDO selectByPrimaryKey(Integer id);

    List<MiddlewareDO> selectByNoteId(@Param("noteId") Integer noteId);

    int updateByPrimaryKeySelective(MiddlewareDO record);

    int updateByPrimaryKey(MiddlewareDO record);
}