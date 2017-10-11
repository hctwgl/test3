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

@Service("redPacketPoolService")
public class AfRedPacketPoolServiceImpl implements AfRedPacketPoolService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
    public  List<BlockingQueue<Redpacket>> list;
    public  int count;
    public  int typeCount;


    @PostConstruct
    public void init(){
        list = new ArrayList<BlockingQueue<Redpacket>>();
        count = 0;
        typeCount = 0;
    }
    @Override
    public void inject(BlockingQueue<Redpacket> packets)  {
        count = count + packets.size();
        list.add(packets);
        typeCount = list.size();
        
        StringBuilder log = new StringBuilder();
        for(BlockingQueue<Redpacket> queue : list) {
        	Redpacket peek = queue.peek();
        	log.append("[queue").append(list.indexOf(queue)).append(",")
        	.append(peek.getCoupon_name()).append(",")
        	.append(peek.getType()).append(",")
        	.append(queue.size()).append("]\r\n");
        }
        logger.info("redPacketPoolService.inject,final pool=\r\n{}", log);
        
    }

    @Override
    public Redpacket apply() {
        int rand = (int)(Math.random() * typeCount)/1;
        BlockingQueue<Redpacket> redpackets = list.get(rand);
        Redpacket r = redpackets.poll();
        return r;
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
