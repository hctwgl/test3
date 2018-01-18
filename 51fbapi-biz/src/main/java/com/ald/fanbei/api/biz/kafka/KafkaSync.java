package com.ald.fanbei.api.biz.kafka;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
    private AfResourceService afResourceService;
    @Autowired
    private AfUserService afUserService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BizCacheUtil bizCacheUtil;
    private static final String COLLECTION_USERDATASUMMARY = "UserDataSummary";
    private static final String COLLECTION_PK = "_id";
    private static final String SYNC_GET_DATA_URL_KEY = "SYNC_GET_DATA_URL_KEY";
    private static final String SYNC_EVENT_DATA_URL = "SYNC_EVENT_DATA_URL";

    /**
     * url同步事件
     * @param userName 用户手机号
     * @param url 当前访问的url
     * @throws Exception
     */
    public void syncEvent(String userName, String url) throws Exception {
        if (userName.length() > 15) {//不是正常的手机号码
            return;
        }
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(SYNC_EVENT_DATA_URL);
        if (afResourceDo == null) {
            return;
        }
        String triggerUrls = afResourceDo.getValue();
        if (triggerUrls.contains(url)) {
            Long userId = afUserService.getUserIdByMobile(userName);
            syncUserSummary(userId);
        }
    }

    /**
     * 用户数据 同步事件
     *
     * @param userId 用户id
     */
    public void syncUserSummary(final Long userId) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                long count = mongoTemplate.count(Query.query(Criteria.where(COLLECTION_PK).is(userId.toString())), COLLECTION_USERDATASUMMARY);
                if (count == 0) {
                    kafkaTemplate.send(KafkaConstants.SYNC_TOPIC, KafkaConstants.SYNC_BORROW_CASH, userId.toString());
                }
            }
        });
    }

    /**
     * 同步获取用户信息
     *
     * @param userId 用户信息
     * @return 用户信息
     */
    public HashMap getUserSummarySync(final Long userId) throws Exception {
        HashMap data = mongoTemplate.findOne(Query.query(Criteria.where(COLLECTION_PK).is(userId.toString())), HashMap.class, COLLECTION_USERDATASUMMARY);
        if (data == null) {
            //同步的方式请求进行数据计算
            HashMap<String, String> paramHashMap = new HashMap();
            paramHashMap.put("userId", userId.toString());
            String baseUrl = getBaseUrl();
            if (baseUrl.indexOf("https") != -1) {
                String result = HttpUtil.doHttpsPostIgnoreCertUrlencoded(getBaseUrl() + "/kafka/getUserSummarySync", getUrlParamsByMap(paramHashMap));
                HashMap hashMap = JSONObject.parseObject(result, HashMap.class);
                return hashMap;
            } else {
                String result = HttpUtil.post(getBaseUrl() + "/kafka/getUserSummarySync", paramHashMap);
                HashMap hashMap = JSONObject.parseObject(result, HashMap.class);
                return hashMap;
            }

        }
        return data;

    }

    /**
     * 获取基础url
     *
     * @return
     * @throws Exception
     */
    public String getBaseUrl() throws Exception {
        Object cacheObj = bizCacheUtil.getObject(SYNC_GET_DATA_URL_KEY);
        String baseUrl = "";
        if (cacheObj == null) {
            AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(SYNC_GET_DATA_URL_KEY);
            if (afResourceDo == null) {
                throw new Exception("please config kafka sync url,key:" + SYNC_GET_DATA_URL_KEY);
            }

            baseUrl = afResourceDo.getValue();
            bizCacheUtil.saveObject(SYNC_GET_DATA_URL_KEY, afResourceDo.getValue());
        } else {
            baseUrl = cacheObj.toString();
        }
        return baseUrl;
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
