package com.ald.fanbei.api.biz.service.impl;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Service;

@Service("redPacketPoolService")
public class AfRedPacketPoolServiceImpl{
	
    public  List<BlockingQueue<String>> list;
    public  int count;
    public  int typeCount;


//    @PostConstruct
//    public void init(){
//        list = new ArrayList<BlockingQueue<String>>();
//        count = 0;
//        typeCount = 0;
//    }
//    @Override
//    public void inject(BlockingQueue<String> packets)  {
//        count = count + packets.size();
//        list.add(packets);
//        typeCount = list.size();
//        
//        logger.info("redPacketPoolService.inject success");
//        
//    }
//
//    @Override
//    public Redpacket apply() {
//    	if(typeCount == 0) {
//    		return null;
//    	}
//    	
//        int rand = (int)(Math.random() * typeCount)/1;
//        BlockingQueue<String> redpackets = list.get(rand);
//        
//        Object o = redpackets.poll();
//        if(o == null) return null;
//        
//        return JSON.parseObject(o.toString(), Redpacket.class);
//    }
//
//    @Override
//    public void emptyPacket() {
//        list.clear();
//    }
//
//    @Override
//    public Map<String,Integer> informationPacket() {
//        Map<String,Integer> map = new HashMap<String,Integer>();
//        int surplusCount = 0;
//        for(int i=0;i<list.size();i++){
//            surplusCount = surplusCount + list.get(i).size();
//        }
//        map.put("listNumber",surplusCount);
//        map.put("count",count);
//        return map;
//    }


}
