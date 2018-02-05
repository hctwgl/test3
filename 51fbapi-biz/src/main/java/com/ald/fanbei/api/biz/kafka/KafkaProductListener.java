package com.ald.fanbei.api.biz.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

/**
 * kafka消息发送监听
 */
public class KafkaProductListener implements ProducerListener {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaProductListener.class);

    /**
     * 消息发送成功时
     * @param topic 主题
     * @param partition 分区
     * @param key 键
     * @param value 值
     * @param metadata 元数据
     */
    @Override
    public void onSuccess(String topic,Integer partition, Object key,Object value,RecordMetadata metadata) {
        logger.info("send success=>"+",topic:"+topic+",partition:"+partition+",key:"+key+",value:"+value,metadata);
    }
    /**
     * 消息发送成功时
     * @param topic 主题
     * @param partition 分区
     * @param key 键
     * @param value 值
     * @param e 异常信息
     */
    @Override
    public void onError(String topic,Integer partition, Object key,Object value, Exception e) {
        //可以记录到数据库做二次处理
        logger.info("send error=>"+",topic:"+topic+",partition:"+partition+",key:"+key+",value:"+value,e);
    }

    /**
     * 是否同时拦截记录发送成功的信息
     * @return
     */
    @Override
    public boolean isInterestedInSuccess() {
        return true;
    }
}
