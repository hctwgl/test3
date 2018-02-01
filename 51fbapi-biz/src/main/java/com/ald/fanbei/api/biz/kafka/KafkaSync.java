package com.ald.fanbei.api.biz.kafka;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * url同步事件
     *
     * @param userName 用户手机号
     * @param url      当前访问的url
     * @throws Exception
     */
    public void syncEvent(String userName, String url, final boolean force) throws Exception {
        if (userName.length() > 15) {//不是正常的手机号码
            return;
        }
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(KafkaConstants.KAFKA_OPEN);
        if (afResourceDo == null || !afResourceDo.getValue().equals("Y")) {
            return;
        }
        List<AfResourceDo> dataTypeUrlList = afResourceService.getConfigByTypes(SYNC_EVENT_DATA_URL);
        for (AfResourceDo resourceDo : dataTypeUrlList) {
            String type = resourceDo.getValue();
            String triggerUrls = resourceDo.getValue1();
            if (triggerUrls.contains(url)) {
                Long userId = afUserService.getUserIdByMobile(userName);
                if (type.contains(KafkaConstants.SYNC_BORROW_CASH)) {
                    syncUserSummary(userId, force);//同步借钱信息
                } else if (type.equals(KafkaConstants.SYNC_USER_BASIC_DATA)) {
                    //同步用户基础信息
                }
            }
        }
    }

    /**
     * 主动调用同步事件
     *
     * @param userId 用户id
     * @param type   事件类型
     * @throws Exception
     */
    public void syncEvent(Long userId, String type, final boolean force) {
        try{
            AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(KafkaConstants.KAFKA_OPEN);
            if (afResourceDo == null || !afResourceDo.getValue().equals("Y")) {
                return;
            }
            if (type.equals(KafkaConstants.SYNC_BORROW_CASH)) {
                //同步用户风控所需的信息
                syncUserSummary(userId, force);//同步借钱信息
            } else if (type.equals(KafkaConstants.SYNC_USER_BASIC_DATA)) {
                kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC), KafkaConstants.SYNC_USER_BASIC_DATA, userId.toString());
            }else if (type.equals(KafkaConstants.SYNC_CASH_LOAN)) {
                //同步用户借钱
                kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC), KafkaConstants.SYNC_CASH_LOAN, userId.toString());
            }else if (type.equals(KafkaConstants.SYNC_CONSUMPTION_PERIOD)) {
                //同步用户基础信息
                kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC), KafkaConstants.SYNC_CONSUMPTION_PERIOD, userId.toString());
            }
        }catch (Exception e){
           logger.error("syncEvent userId:"+userId+",type="+type,e);
        }
    }

    /**
     * 用户数据 同步事件
     *
     * @param userId 用户id
     * @param force  是否强制刷新
     */
    private void syncUserSummary(final Long userId, final boolean force) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                if (force) {
                    kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC), KafkaConstants.SYNC_BORROW_CASH, userId.toString());
                } else {
                    long count = mongoTemplate.count(Query.query(Criteria.where(COLLECTION_PK).is(userId.toString())), COLLECTION_USERDATASUMMARY);
                    if (count == 0) {
                        kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC), KafkaConstants.SYNC_BORROW_CASH, userId.toString());
                    }
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
