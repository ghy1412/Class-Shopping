package com.dreamone.controller;

import com.dreamone.controller.viewobject.ItemVo;
import com.dreamone.dao.ItemDOMapper;
import com.dreamone.error.BusinessException;
import com.dreamone.response.CommonReturnType;
import com.dreamone.service.CacheService;
import com.dreamone.service.impl.ItemServiceImpl;
import com.dreamone.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    //创建商品
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMER})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);
        itemModel.setStock(stock);
        itemModel.setPrice(price);
        System.out.println(itemModel);
        ItemModel item = itemService.createItem(itemModel);
        ItemVo itemVo = convertVoFromModel(item);
        return CommonReturnType.create(itemVo);
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        //现在从缓存中试试
        ItemModel itemModel = (ItemModel) cacheService.getFromCommonCache("item_" + id);

        if (itemModel == null) {
            //redis中取
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_"+id);
            if (itemModel == null) {
                //缓存中没有,然后从数据库中获取存入缓存
                itemModel = itemService.getItem(id);
                redisTemplate.opsForValue().set("item_"+id,itemModel);
                redisTemplate.expire("item_"+id,6, TimeUnit.SECONDS);
            }
            cacheService.setCommonCache("item_"+id,itemModel);
        }

        ItemVo itemVo = convertVoFromModel(itemModel);
        return CommonReturnType.create(itemVo);
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType list() {
        List<ItemModel> itemModels = itemService.listItem();
        //使用streamAPI
        List<ItemVo> collect = itemModels.stream().map(itemModel -> {
            ItemVo itemVo = convertVoFromModel(itemModel);
            return itemVo;
        }).collect(Collectors.toList());
        return CommonReturnType.create(collect);
    }


    private ItemVo convertVoFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel, itemVo);
        if (itemModel.getPromoModel() != null) {
            itemVo.setPromoStatus(itemModel.getPromoModel().getStatus());
            System.out.println(itemModel.getPromoModel().getStatus());
            itemVo.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVo.setPromoStatus(0);
        }
        return itemVo;
    }
}
