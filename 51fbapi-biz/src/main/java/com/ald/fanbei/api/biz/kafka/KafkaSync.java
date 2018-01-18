package com.ald.fanbei.api.biz.kafka;

import com.ald.fanbei.api.common.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class KafkaSync {
    ExecutorService pool = Executors.newFixedThreadPool(16);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final String COLLECTION_USERDATASUMMARY = "UserDataSummary";
    private static final String COLLECTION_PK = "_id";
    private static final String SYNC_GET_DATA_URL = "UserDataSummary";
    /**
     * 用户数据 同步事件
     * @param userId 用户id
     */
    public void syncUserSummary(final Long userId) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                long count = mongoTemplate.count(Query.query(Criteria.where(COLLECTION_PK).is(userId.toString())), COLLECTION_USERDATASUMMARY);
                if (count == 0) {
                    kafkaTemplate.send(KafkaConstants.SYNC_TOPIC, userId.toString());
                }
            }
        });
    }

    /**
     * 同步获取用户信息
     * @param userId 用户信息
     * @return 用户信息
     */
    public HashMap getUserSummarySync(final Long userId) {
        HashMap data = mongoTemplate.findOne(Query.query(Criteria.where(COLLECTION_PK).is(userId.toString())),HashMap.class, COLLECTION_USERDATASUMMARY);
        if(data==null){
            //同步的方式请求进行数据计算
            HashMap paramHashMap=new HashMap();
            paramHashMap.put("userId",userId);
            String result=HttpUtil.doHttpsPostIgnoreCertUrlencoded("http://localhost:8006/kafka/getUserSummarySync",getUrlParamsByMap(paramHashMap));
            HashMap hashMap= JSONObject.parseObject(result,HashMap.class);
            return hashMap;
        }
        return data;

    }
    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

}
