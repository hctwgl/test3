package com.ald.fanbei.api.common.util.dingding;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

public class DingdingUtil {


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

    public static void sendMessageByRobot(String robotUrl, String message, Boolean atAll) {
        if (atAll == null || !atAll) {
            sendMessageByRobot(robotUrl, message);
            return;
        }
        DingdingData dingData = DingdingData.create(message).setIsAtAll();
        doSendMessage(robotUrl, dingData);
    }
}
