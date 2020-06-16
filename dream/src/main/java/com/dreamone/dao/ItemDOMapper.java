package com.dreamone.dao;

import com.dreamone.dataobject.ItemDO;
import com.dreamone.dataobject.ItemDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;

public interface ItemDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    List<ItemDO> selectByExample(ItemDOExample example);

    ItemDO selectByPrimaryKey(Integer id);

    List<ItemDO> listItem();

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    void increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);
}