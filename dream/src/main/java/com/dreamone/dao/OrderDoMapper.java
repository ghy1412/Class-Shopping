package com.dreamone.dao;

import com.dreamone.dataobject.OrderDo;
import com.dreamone.dataobject.OrderDoExample;
import java.util.List;

public interface OrderDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderDo record);

    int insertSelective(OrderDo record);

    List<OrderDo> selectByExample(OrderDoExample example);

    OrderDo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDo record);

    int updateByPrimaryKey(OrderDo record);
}