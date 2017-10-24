package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfRedPacketPoolService;
import com.alibaba.fastjson.JSON;

@Service("redPacketPoolService")
public class AfRedPacketPoolServiceImpl implements AfRedPacketPoolService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
    public  List<BlockingQueue<String>> list;
    public  int count;
    public  int typeCount;


    @PostConstruct
    public void init(){
        list = new ArrayList<BlockingQueue<String>>();
        count = 0;
        typeCount = 0;
    }
    @Override
    public void inject(BlockingQueue<String> packets)  {
        count = count + packets.size();
        list.add(packets);
        typeCount = list.size();
        
        logger.info("redPacketPoolService.inject success");
        
    }

    @Override
    public Redpacket apply() {
    	if(typeCount == 0) {
    		return null;
    	}
    	
        int rand = (int)(Math.random() * typeCount)/1;
        BlockingQueue<String> redpackets = list.get(rand);
        
        Object o = redpackets.poll();
        if(o == null) return null;
        
        return JSON.parseObject(o.toString(), Redpacket.class);
    }

    @Override
    public void emptyPacket() {
        list.clear();
    }

    @Override
    public Map<String,Integer> informationPacket() {
        Map<String,Integer> map = new HashMap<String,Integer>();
        int surplusCount = 0;
        for(int i=0;i<list.size();i++){
            surplusCount = surplusCount + list.get(i).size();
        }
        map.put("listNumber",surplusCount);
        map.put("count",count);
        return map;
    }


}
