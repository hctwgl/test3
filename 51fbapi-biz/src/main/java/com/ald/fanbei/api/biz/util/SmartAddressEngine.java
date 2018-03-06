package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SmartAddressEngine {
    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    public static ExecutorService pool = Executors.newFixedThreadPool(8);
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    public static int getScore(String address){
        try {
            logger.info("百度地址评分："+address);
            String result = HttpUtil.doGet("http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=6eea93095ae93db2c77be9ac910ff311",3000);
            JSONObject json=JSONObject.parseObject(result) ;
            if(json.getString("status").equals("0")){
                logger.info("---------address:"+address+"---------score:"+json.getJSONObject("result").getInteger("confidence"));
                return json.getJSONObject("result").getInteger("confidence");
            }
        }catch (Exception e){
            logger.info("获取百度地址评分异常{}",e);
        }
        return 0;
    }
    public  void setScoreAsyn(final String address,final long borrowid,final String orderno){
        pool.execute(new Runnable() {
            @Override
            public void run() {
               int score = SmartAddressEngine.getScore(address);
                afBorrowLegalOrderService.updateSmartAddressScore(score,borrowid,orderno);
            }
        });
    }

}
