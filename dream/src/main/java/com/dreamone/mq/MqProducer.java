package com.dreamone.mq;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.dreamone.dao.StockLogDOMapper;
import com.dreamone.dataobject.StockLogDO;
import com.dreamone.error.BusinessException;
import com.dreamone.service.OrderService;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqProducer {

    private DefaultMQProducer  defaultMQProducer = null;

    private TransactionMQProducer transactionMQProducer = null;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Value("${mq.server.addr}")
    private String nameAddr;

    @Value("${mq.topicName}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        defaultMQProducer = new DefaultMQProducer("producerGroup1");
        defaultMQProducer.setNamesrvAddr(nameAddr);
        defaultMQProducer.start();

        transactionMQProducer = new TransactionMQProducer("tranGroup");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //在这个方法里面执行相应的方法， 相应也要注释掉以前写的事务完成后在进行发送消息
                Integer userId = (Integer) ((Map)o).get("userId");
                Integer itemId = (Integer) ((Map)o).get("itemId");
                Integer promoId = (Integer) ((Map)o).get("promoId");
                Integer amount = (Integer) ((Map)o).get("amount");
                String stockLogId = (String) ((Map)o).get("stockLogId");
                try {
                    orderService.createOrder(userId, itemId,promoId,amount,stockLogId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    //在这里设定stockLog等于三
                    StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                    stockLogDO.setStatus(3);
                    stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String jsonString = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId = (Integer) (map).get("itemId");
                Integer amount = (Integer) (map).get("amount");
                String stockLogId = (String) (map).get("stockLogId");
                StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                if (stockLogDO == null) {
                    return LocalTransactionState.UNKNOW;
                }
                if (stockLogDO.getStatus().intValue() == 1) {
                    return LocalTransactionState.UNKNOW;
                } else if (stockLogDO.getStatus().intValue() == 2) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });
        transactionMQProducer.start();
    }


    //事务性产生消息
    public boolean transactionReduceStock(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId){
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("amount", amount);
        map.put("stockLogId",stockLogId);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);
        argsMap.put("stockLogId",stockLogId);

        Message message = new Message(topicName, "increase", JSON.toJSONBytes(map));
        try {
            TransactionSendResult transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
            if (transactionSendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
                return true;
            } else if (transactionSendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE){
                return false;
            } else {
                return false;
            }
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("amount", amount);
        Message message = new Message(topicName, "increase", JSON.toJSONBytes(map));
        try {
            defaultMQProducer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
