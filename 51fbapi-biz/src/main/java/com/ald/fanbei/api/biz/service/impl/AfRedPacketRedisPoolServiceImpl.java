package com.ald.fanbei.api.biz.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfRedPacketPoolService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.alibaba.fastjson.JSON;

@Service("redPacketRedisPoolService")
public class AfRedPacketRedisPoolServiceImpl implements AfRedPacketPoolService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private BizCacheUtil bizCacheUtil;
    
    @PostConstruct
    public void init(){
    	
    }
    @Override
    public void inject(BlockingQueue<String> packets)  {
    	String queueName = Constants.CACHEKEY_REDRAIN_QUEUE_NAME_PREFFIX;
    	int queueSum = bizCacheUtil.keys(Constants.CACHEKEY_REDRAIN_QUERY_KEYS_PATTERN).size();
    	queueName = queueName + queueSum;
    	
    	bizCacheUtil.lpush(queueName, packets);
    	bizCacheUtil.hincrBy(Constants.CACHEKEY_REDRAIN_MISC, "sumPacketsCurRound", (long)packets.size());
    	
        logger.info("redPacketPoolService.inject success");
    }

    @Override
    public Redpacket apply() {
    	int queueSum = bizCacheUtil.keys(Constants.CACHEKEY_REDRAIN_QUERY_KEYS_PATTERN).size();
    	
    	if(queueSum == 0) {
    		return null;
    	}
    	
        int rand = (int)(Math.random() * queueSum)/1;
        String queueName = Constants.CACHEKEY_REDRAIN_QUEUE_NAME_PREFFIX + rand;
        
        Object o = bizCacheUtil.rpop(queueName);
        if(o == null) return null;
        
        return JSON.parseObject(o.toString(), Redpacket.class);
    }

    @Override
    public void emptyPacket() {
        bizCacheUtil.del(bizCacheUtil.keys(Constants.CACHEKEY_REDRAIN_QUERY_KEYS_PATTERN));
        bizCacheUtil.delCache(Constants.CACHEKEY_REDRAIN_MISC);
    }

    @Override
    public Map<String,Integer> informationPacket() {
    	Collection<String> keys = bizCacheUtil.keys(Constants.CACHEKEY_REDRAIN_QUERY_KEYS_PATTERN);
    	int surplus = 0;
    	for(String queueName : keys) {
    		surplus += bizCacheUtil.llen(queueName);
    	}
    	
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("listNumber",surplus);
        map.put("count", Integer.valueOf(bizCacheUtil.hget(Constants.CACHEKEY_REDRAIN_MISC, "sumPacketsCurRound")));
        
        return map;
    }

}
