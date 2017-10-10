package com.ald.fanbei.api.biz.service.redpacket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Service("redPacketPoolService")
public class RedPacketPoolServiceImpl implements RedPacketPoolService {
    @Resource
    RedisTemplate redisTemplate;

    public  List<BlockingQueue<Redpacket>> list;
    public  int count;
    public  int typeCount;

    public AtomicInteger applyCounter = new AtomicInteger();

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

    }

    @Override
    public Redpacket apply() {
        int rand = (int)(Math.random() * typeCount)/1;
        BlockingQueue<Redpacket> redpackets = list.get(rand);
        System.out.println(rand);
        Redpacket r = redpackets.poll();
        if(r != null){
            applyCounter.incrementAndGet();
        }
        return r;
    }

    @Override
    public void EmptyPacket() {
        list.clear();
    }

    @Override
    public Map<String,Integer> InformationPacket() {
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("listNumber",list.size());
        map.put("count",count);
        return map;
    }

    public static void main (String[] args) {

        final RedPacketPoolServiceImpl  aa = new RedPacketPoolServiceImpl();
        aa.init();
        for (int x=0;x<10;x++){
            BlockingQueue<Redpacket> packets = new ArrayBlockingQueue<Redpacket>(2000000);
            for(int i=0;i<10000;i++){
                RedPacketPoolService.Redpacket redpacket = new RedPacketPoolService.Redpacket();
                redpacket.setType("CASH");
                redpacket.setCoupon_id(1l);
                redpacket.setCoupon_name("优惠券");
                packets.add(redpacket);
            }


            aa.inject(packets);
        }
//        System.out.println(JSON.toJSONString(aa));
       final CountDownLatch latch = new CountDownLatch(5000);

        for(int y=0;y<5000;y++){
            new Thread(){
                @Override
                public void run() {
                    for(int i=0;i<40;i++){
                        System.out.println(JSON.toJSONString(
                                aa.apply()
                        ));
                    }
                    latch.countDown();
                }
            }.start();

        }
        try {
            latch.await();
        }catch (Exception e){

        }

        System.out.println(JSON.toJSONString(aa));

    }

}
