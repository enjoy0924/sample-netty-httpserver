package com.alr.component.mq;

import org.apache.commons.collections.map.HashedMap;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by G_dragon on 2017/7/31.
 *
 */
public class RocketMQAdapter {

    private static String producerGroup = "producerGroup";
    private static String consumerGroup = "consumerGroup";
    private static String defaultNameServer = "192.168.0.115:9876";
    private static int initialState = 0;

    private static Map<String,DefaultMQPushConsumer> topic2Consumer = new HashedMap();

    public static synchronized void initializeAdapter(String producerGroup, String consumerGroup, String nameServer) {
        if (initialState == 0) {
            if(null!=producerGroup&&!"".equals(producerGroup.trim())){
                RocketMQAdapter.producerGroup = producerGroup;
            }
            if(null!=consumerGroup&&!"".equals(consumerGroup.trim())){
                RocketMQAdapter.consumerGroup = consumerGroup;
            }
            if(null!=nameServer&&!"".equals(nameServer.trim())){
                RocketMQAdapter.defaultNameServer = nameServer;
            }
            RocketMQAdapter.initialState = 1;

            Producer.getDefaultMQProducer();
        }
    }

    public static class Producer {
        /*
        * Constructs a client instance with your account for accessing DefaultMQProducer
        */
        private static DefaultMQProducer producer;
        private static Date nextTrySendDate=new Date();

        private Producer() {
        }

        private static synchronized DefaultMQProducer getDefaultMQProducer() {
            if (producer != null) {
                return producer;
            }
            if (initialState == 0) {
                return null;
            } else {
                try {
                    producer = new DefaultMQProducer(producerGroup);
                    producer.setNamesrvAddr(defaultNameServer);
                    producer.start();
                    return producer;
                } catch (MQClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    producer = null;
                    return null;
                }
            }
        }

        public static void AsyncSend(Message message, SendCallback sendCallback) {
            try {
                if (new Date().before(nextTrySendDate)) {
                    //如果MQ异常  则几秒尝试一次
                    RecordMessage(message);
                } else {
                    DefaultMQProducer producer = Producer.getDefaultMQProducer();
                    if (producer != null) {
                        producer.send(message, sendCallback);
                    } else {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.SECOND, 5);
                nextTrySendDate = c.getTime();
                RecordMessage(message);
                sendCallback.onException(e);
            }
        }

        private static void RecordMessage(Message message) {
            //TODO:将本次发送的消息记录下来
        }
    }

    public static class Consumer {

        private Consumer() {

        }

        public static DefaultMQPushConsumer getDefaultMQPushConsumer(String topic) {
            if (initialState == 0) {
                return null;
            } else {
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup + topic);
                consumer.setNamesrvAddr(defaultNameServer);
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                return consumer;
            }
        }
    }

    public static void registerMsg2RecvAndHandle(String topic, String subTags, MessageListenerConcurrently messageListener){

        if(topic2Consumer.containsKey(topic)){
            topic2Consumer.get(topic).shutdown();
            topic2Consumer.remove(topic);
        }

        // 获取消息生产者
        DefaultMQPushConsumer consumer = Consumer.getDefaultMQPushConsumer(topic);

        // 订阅主体
        try {
            consumer.subscribe(topic, subTags);

            consumer.registerMessageListener(messageListener);
            /**
             * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
             */
            consumer.start();

        } catch (MQClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

