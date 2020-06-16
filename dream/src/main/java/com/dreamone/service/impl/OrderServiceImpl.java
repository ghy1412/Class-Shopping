package com.dreamone.service.impl;

import com.dreamone.dao.*;
import com.dreamone.dataobject.*;
import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.mq.MqProducer;
import com.dreamone.service.OrderService;
import com.dreamone.service.UserService;
import com.dreamone.service.model.ItemModel;
import com.dreamone.service.model.OrderModel;
import com.dreamone.service.model.UserModel;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private OrderDoMapper orderDoMapper;

    @Autowired
    private sequenceDOMapper sequenceDOMapper;

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Autowired
    private MqProducer mqProducer;
    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException {
        //校验商品是否存在, 用户是否合法, 购买数量是否正确
        //里面包含了打折信息
        //ItemModel itemModel = itemService.getItem(itemId);
        ItemModel itemModel = itemService.getItemFromRedis(itemId);

        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }

        System.out.println("用户的Id: " + userId);
        //UserDO userDO = userDOMapper.selectByPrimaryKey(userId);
        UserModel userModel = userService.getUserFromRedis(userId);


        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }

        if (amount <= 0) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量错误");
        }
        //落单减库存, /////////支付减库存
        //若已近发送消息， 库存扣减成功， 但下面创建订单失败， 会造成库存少
        //无法确认以后代码会成功
        boolean b = itemService.decreaseStock(itemId, amount);

        if (!b) {
            throw  new BusinessException(EmBusinessError.ORDER_NOT_ENOUGH);
        }

        BigDecimal price = itemModel.getPrice();

        //有无秒杀价格

        if (promoId != null) {
            if (!promoId.equals(itemModel.getPromoModel().getId())) {
                throw new BusinessException(EmBusinessError.UNKNOW_ERROR,"秒杀活动不存在!");
            }else if (itemModel.getPromoModel().getStatus() == 1) {
                throw new BusinessException(EmBusinessError.UNKNOW_ERROR,"秒杀活动未开始!");
            } if (itemModel.getPromoModel().getStatus() == 3) {
                throw new BusinessException(EmBusinessError.UNKNOW_ERROR,"秒杀活动结束!");
            } else {
                price = itemModel.getPromoModel().getPromoItemPrice();
            }
        }

        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setUsesId(userId);
        orderModel.setAmount(amount);

        orderModel.setId(generateOrderNumber());

        orderModel.setItemPrice(price);

        orderModel.setOrderPrice(price.multiply(new BigDecimal(amount.toString())));
        //返回前端
        //String id = generateOrderNumber();

        OrderDo orderDo = convertDoFromOrderModel(orderModel);
        orderDoMapper.insertSelective(orderDo);

        //增加销量
        itemService.increaseSales(itemId,amount);

        //最后扣减异步更新库存
/*        boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
        if (!mqResult) {
            itemService.increaseStock(itemId,amount);
            throw new BusinessException(EmBusinessError.ASYNCDECREASE_FAIL);
        }*/
//事务完成才会执行的方法, 但消息一旦发送不出去就会永远丢失
/*        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
                //这条消息不能失败, 失败就永远丢失了,so注释掉, 用rocketMq的事务性消息
            }
        });*/

//返回前端之前修改状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDO == null) {
            throw new BusinessException(EmBusinessError.ASYNCDECREASE_FAIL,"库存流水线为空");

        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
        //返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNumber() {

        StringBuilder sb = new StringBuilder();
        //16位
        LocalDateTime nowDate = LocalDateTime.now();
        //前八位为时间信息

        String date = nowDate.format(DateTimeFormatter.ISO_DATE);

        sb.append(date.replace("-",""));
        //中间6位为自增序列

        sequenceDO order_info = sequenceDOMapper.getSequenceByName("order_info");
        Integer currentValue = order_info.getCurrentValue();
        order_info.setCurrentValue(currentValue+order_info.getStep());

        //sequenceDOMapper.insertSelective(order_info);
        sequenceDOMapper.updateByPrimaryKeySelective(order_info);

        String sequence = String.valueOf(currentValue);

        for (int i = 0; i < 6-sequence.length(); i++) {
            sb.append(0);
        }
        sb.append(sequence);
        //两位为分库分表,  比如通过userID分   userID%10
        sb.append("00");
        return sb.toString();
    }

    private OrderDo convertDoFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDo orderDo1 = new OrderDo();
        BeanUtils.copyProperties(orderModel,orderDo1);
        return orderDo1;
    }
}
