package com.ald.fanbei.api.biz.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfRedPacketPoolService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;

@Service("redPacketRedisPoolService")
public class AfRedPacketRedisPoolServiceImpl implements AfRedPacketPoolService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private BizCacheUtil bizCacheUtil;
    
    @PostConstruct
    public void init(){
    	
    }
    @Override
    public void inject(Collection<String> packets)  {
    	bizCacheUtil.lpush(Constants.CACHEKEY_REDRAIN_SINK, packets);
    	bizCacheUtil.hincrBy(Constants.CACHEKEY_REDRAIN_MISC, "sumPacketsCurRound", (long)packets.size());
    	
        logger.info("redPacketPoolService.inject success");
    }

    @Override
    public String apply() {
    	int queueSum = bizCacheUtil.keys(Constants.CACHEKEY_REDRAIN_SINK).size();
    	if(queueSum == 0) {
    		return null;
    	}
        
        Object o = bizCacheUtil.rpop(Constants.CACHEKEY_REDRAIN_SINK);
        if(o == null) return null;
        
        return o.toString();
    }

    @Override
    public void emptyPacket() {
        bizCacheUtil.delCache(Constants.CACHEKEY_REDRAIN_SINK);
        bizCacheUtil.delCache(Constants.CACHEKEY_REDRAIN_MISC);
    }

    @Override
    public Map<String,Integer> informationPacket() {
    	int surplus = (int) bizCacheUtil.llen(Constants.CACHEKEY_REDRAIN_SINK);
    	
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("listNumber",surplus);
        map.put("count", Integer.valueOf(bizCacheUtil.hget(Constants.CACHEKEY_REDRAIN_MISC, "sumPacketsCurRound")));
        
        return map;
    }

}
