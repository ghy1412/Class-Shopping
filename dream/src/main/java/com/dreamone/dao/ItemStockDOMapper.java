package com.dreamone.dao;

import com.dreamone.dataobject.ItemStockDO;
import com.dreamone.dataobject.ItemStockDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemStockDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);

    List<ItemStockDO> selectByExample(ItemStockDOExample example);

    ItemStockDO selectByPrimaryKey(Integer id);

    ItemStockDO selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(ItemStockDO record);

    int updateByPrimaryKey(ItemStockDO record);

    int decreaseStock(@Param("item_id") Integer item_id, @Param("amount") Integer amount);
}