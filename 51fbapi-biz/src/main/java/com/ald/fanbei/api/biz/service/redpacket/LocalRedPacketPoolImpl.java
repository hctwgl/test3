package com.ald.fanbei.api.biz.service.redpacket;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LocalRedPacketPoolImpl implements RedpacketPool {
    @Resource
    RedisTemplate redisTemplate;



    @Override
    public void inject(BlockingQueue<Redpacket> packets)   {
        List list = new ArrayList();
        int count = packets.size();
        list.add(packets);

    }

    @Override
    public Redpacket apply() {
        return null;
    }

    @Override
    public void EmptyPacket() {

    }

    @Override
    public void InformationPacket() {

    }
}
