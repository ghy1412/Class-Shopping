package com.dreamone.dao;

import com.dreamone.dataobject.StockLogDO;
import com.dreamone.dataobject.StockLogDOExample;
import java.util.List;

public interface StockLogDOMapper {
    int deleteByPrimaryKey(String stockLogId);

    int insert(StockLogDO record);

    int insertSelective(StockLogDO record);

    List<StockLogDO> selectByExample(StockLogDOExample example);

    StockLogDO selectByPrimaryKey(String stockLogId);

    int updateByPrimaryKeySelective(StockLogDO record);

    int updateByPrimaryKey(StockLogDO record);
}