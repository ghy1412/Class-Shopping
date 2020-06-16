package com.dreamone.mq;

import com.alibaba.fastjson.JSON;
import com.dreamone.dao.ItemStockDOMapper;
import com.dreamone.service.ItemService;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class MqConsumer {

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Value("${mq.server.addr}")
    private String nameAddr;

    @Value("${mq.topicName}")
    private String topicName;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
        defaultMQPushConsumer = new DefaultMQPushConsumer();
        defaultMQPushConsumer.setNamesrvAddr(nameAddr);
        defaultMQPushConsumer.subscribe(topicName,"*");
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //实现数据库扣减的操作
                MessageExt messageExt = list.get(0);
                String jsonString = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                itemStockDOMapper.decreaseStock((Integer) map.get("itemId"), (Integer) map.get("amount"));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumer.start();
    }
}
