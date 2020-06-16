package com.dreamone.service.impl;

import com.dreamone.dao.ItemDOMapper;
import com.dreamone.dao.ItemStockDOMapper;
import com.dreamone.dao.StockLogDOMapper;
import com.dreamone.dataobject.ItemDO;
import com.dreamone.dataobject.ItemStockDO;
import com.dreamone.dataobject.StockLogDO;
import com.dreamone.dataobject.UserDO;
import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.mq.MqProducer;
import com.dreamone.service.ItemService;
import com.dreamone.service.model.ItemModel;
import com.dreamone.service.model.PromoModel;
import com.dreamone.validator.ValidationResult;
import com.dreamone.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoServiceImpl promoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer producer;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //参数校验
        ValidationResult validate = validator.validate(itemModel);
        System.out.println("校验itemModel");
        if (validate.hasErrors) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validate.getErrorMsg());
        }
        System.out.println("item校验成功");
        //取得ItemDO ItemStockDo 对象
        ItemDO itemDO = converItemDOFromModel(itemModel);

        //插入数据库
        System.out.println(itemDO);
        itemDO.setSales(0);
        itemDOMapper.insertSelective(itemDO);
        System.out.println("插入商品名");
        ItemStockDO itemStockDO = converItemStockDoFromModel(itemModel);
        System.out.println("id"+itemDO.getId());
        itemStockDO.setItemId(itemDO.getId());

        itemStockDOMapper.insertSelective(itemStockDO);
        //返回ItemModel对象
        System.out.println("插入库存");
        return this.getItem(itemDO.getId());
    }

    private ItemStockDO converItemStockDoFromModel(ItemModel itemModel){
        ItemStockDO itemStockDO = new ItemStockDO();
        if (itemModel == null) {
            return null;
        }
        BeanUtils.copyProperties(itemModel, itemStockDO);
        return itemStockDO;
    }
    private ItemDO converItemDOFromModel(ItemModel itemModel) {
        ItemDO itemDO = new ItemDO();
        if (itemModel == null) {
            return null;
        }
        BeanUtils.copyProperties(itemModel, itemDO);
        return itemDO;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModels = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.converItemModel(itemDO, itemStockDO);
            return itemModel;

        }).collect(Collectors.toList());
        return itemModels;
    }

    @Override
    public ItemModel getItem(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(itemId);
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = converItemModel(itemDO, itemStockDO);
        PromoModel promoModelByItemId = promoService.getPromoModelByItemId(itemId);
        if (promoModelByItemId != null && promoModelByItemId.getStatus() != 3) {
            itemModel.setPromoModel(promoModelByItemId);
        }
        return itemModel;
    }

    @Override
    public ItemModel getItemFromRedis(Integer keyId) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_valid"+keyId);
        if (itemModel == null) {
            itemModel = this.getItem(keyId);
            redisTemplate.opsForValue().set("item_valid"+keyId,itemModel);
            redisTemplate.expire("item_valid"+keyId,60, TimeUnit.SECONDS);
        }
        return itemModel;
    }

    @Override
    @Transactional //依赖于外部事务， 同时成功失败
    public boolean decreaseStock(Integer item_id, Integer amount) {
        //调用SQl
        //int affectedRows = itemStockDOMapper.decreaseStock(item_id, amount);

        //改用redis, 活动开始前, 添加库存到对应的数据
        Long increment = redisTemplate.opsForValue().increment("amount_item_" + item_id, amount.intValue() * -1);

        if (increment >= 0) {
            //成功
/*            boolean ret =  producer.asyncReduceStock(item_id,amount);
            if (ret == false) {
                redisTemplate.opsForValue().increment("amount_item_" + item_id,amount.intValue());
            }
            return ret;*/
            return true;
        } else {
            //失败
            redisTemplate.opsForValue().increment("amount_item_" + item_id,amount.intValue());
            return false;
        }
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("amount_item_" + itemId,amount.intValue());
        //不考虑redis失败
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        boolean ret =  producer.asyncReduceStock(itemId,amount);
        return ret;
    }

    @Override
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setAmount(amount);
        stockLogDO.setItemId(itemId);
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId,amount);
    }

    private ItemModel converItemModel(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
