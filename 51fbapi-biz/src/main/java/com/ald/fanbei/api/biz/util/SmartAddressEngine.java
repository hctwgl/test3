package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.common.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmartAddressEngine {
    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    public int getScore(String address){
       JSONObject json=JSONObject.parseObject(HttpUtil.doGet("http://api.map.baidu.com/geocoder?address="+address+"&output=json&key=6eea93095ae93db2c77be9ac910ff311",5000)) ;
       if(json.getString("status").equals("OK")){
           logger.info("---------address:"+address+"---------score:"+json.getJSONObject("result").getInteger("confidence"));
           return json.getJSONObject("result").getInteger("confidence");
       }
       return 0;
    }
}
