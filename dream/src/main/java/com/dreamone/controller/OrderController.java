package com.dreamone.controller;

import com.alibaba.druid.util.StringUtils;
import com.dreamone.dao.OrderDoMapper;
import com.dreamone.error.BusinessException;
import com.dreamone.error.EmBusinessError;
import com.dreamone.mq.MqProducer;
import com.dreamone.response.CommonReturnType;
import com.dreamone.service.ItemService;
import com.dreamone.service.impl.OrderServiceImpl;
import com.dreamone.service.model.OrderModel;
import com.dreamone.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderServiceImpl orderServiceimpl;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/createOrder")
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId",required = false)Integer promoId) throws BusinessException {


        System.out.println("购买东西开始");
/*
        Boolean is_login = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
*/
/*        if (is_login == null || !is_login) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }*/


        String token = httpServletRequest.getParameterMap().get("token")[0];
        System.out.println("token "+token);
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

/*
        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("USER_LOGIN");
*/

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        System.out.println("从缓存中取得的用户模型ID: "+userModel.getId());
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        Boolean soldOut = redisTemplate.hasKey("item_sold_out" + itemId);
        if (soldOut) {
            throw new BusinessException(EmBusinessError.ORDER_NOT_ENOUGH,"商品库存不足");
        }
        //创建订单开始前, 先初始换库存流水./ 但一瞬间amount不够, 还创建,创建太多无用, 设置售罄标志
        String stockLogString = itemService.initStockLog(itemId, amount);
        //OrderModel orderModel = orderServiceimpl.createOrder(userModel.getId(), itemId, promoId, amount);
        //这里调用rocketMq的事务性机制, 调用createOrder, 保证broker能得知到底交易事务完成没有, 发送消息,也就是置换消息的状态
        boolean mqResult = mqProducer.transactionReduceStock(userModel.getId(), itemId, promoId, amount, stockLogString);
        if (!mqResult) {
            throw new BusinessException(EmBusinessError.ASYNCDECREASE_FAIL, "消息发送失败或者库存扣减失败");
        }
        return CommonReturnType.create(null);
    }
}
