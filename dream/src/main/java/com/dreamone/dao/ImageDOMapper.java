package com.dreamone.dao;

import com.dreamone.dataobject.ImageDO;
import com.dreamone.dataobject.ImageDOExample;
import java.util.List;

public interface ImageDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ImageDO record);

    int insertSelective(ImageDO record);

    List<ImageDO> selectByExample(ImageDOExample example);

    List<ImageDO> selectImage(Integer first, Integer last);

    ImageDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ImageDO record);

    int updateByPrimaryKey(ImageDO record);
}