package com.dreamone.service;

import com.dreamone.error.BusinessException;
import com.dreamone.service.model.ItemModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //浏览商品
    List<ItemModel> listItem();

    //商品详情浏览页
    ItemModel getItem(Integer itemId);

    ItemModel getItemFromRedis(Integer keyId);
    //库存落单
    boolean decreaseStock(Integer itemId, Integer amount);
    //库存回补
    boolean increaseStock(Integer itemId, Integer amount);
    //异步更新库存
    boolean asyncDecreaseStock(Integer itemId, Integer amount);
    //初始化库存流水
    String initStockLog(Integer itemId, Integer amount);

    void increaseSales(Integer itemId, Integer amount);
}
