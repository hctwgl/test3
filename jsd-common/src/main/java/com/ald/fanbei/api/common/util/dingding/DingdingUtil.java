package com.ald.fanbei.api.common.util.dingding;

import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

public class DingdingUtil {

    private static String INVELOMENT_TYPE = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=25e746714401a5f51249ffe4b9796325ee83e38d76f7e9122a7202c4835b6968";


    /**
     * 通过钉钉机器人发送钉钉消息
     *
     * @param robotUrl 机器人地址
     * @param message  消息内容
     * @return
     */
    public static String sendMessageByRobot(String robotUrl, String message) {
        DingdingData dingData = DingdingData.create(message);
        return doSendMessage(robotUrl, dingData);
    }

    private static String doSendMessage(String robotUrl, DingdingData dingData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setReadTimeout(1000);//单位为ms
            factory.setConnectTimeout(1000);//单位为ms
            HttpEntity<DingdingData> formEntity = new HttpEntity(dingData, headers);
            String data = new RestTemplate(factory).postForObject(robotUrl, formEntity, String.class);

            System.out.println(data);
            return data;
        } catch (Exception ex) {
            System.out.println(String.format("钉钉消息发送失败:%s,data:%s", ex.getMessage(), JSON.toJSONString(dingData)));
        }
        return null;
    }

//    public static void sendMessageByRobot(String robotUrl, String message, String[] atMobiles) {
//        if (atMobiles == null || atMobiles.length == 0) {
//            sendMessageByRobot(robotUrl, message);
//            return;
//        }
//        DingdingData dingData = DingdingData.create(message).setAtMobiles(atMobiles);
//        doSendMessage(robotUrl, dingData);
//    }

    public static void sendMessageByJob(String message, Boolean atAll) {
        if(StringUtils.equals(INVELOMENT_TYPE,"test")){
           return;
        }
        String robotUrl = WEBHOOK_TOKEN;
        if (atAll == null || !atAll) {
            sendMessageByRobot(robotUrl, message);
            return;
        }
        DingdingData dingData = DingdingData.create(message).setIsAtAll();
        doSendMessage(robotUrl, dingData);
    }
}
