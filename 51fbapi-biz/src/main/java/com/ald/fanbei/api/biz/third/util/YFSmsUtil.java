package com.ald.fanbei.api.biz.third.util;


import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 云峰短信对接
 *
 * @author xieqiang
 * @create 2018-01-03 20:13
 **/
public class YFSmsUtil {
    public final static String URL = "https://api.cloudfeng.com/api/v2/sendSms.json";
    public final static String appKey = "p46ldtbsh8mwvyr8hnrnqba4z1tabv78";
    public final static String appSecret = "cfadcd791ce4712d2018a03a7f68150b2901";
    public final static String appKeyPromotion = "2h7mfejk8wdhb1x6d772ozt2dx6dxqpr";
    public final static String appSecretPromotion = "d552a175dd0bb1a543c75b54e1f7219d4227";
    public final static String VERIFYCODE = "1";//验证码
    public final static String NOTITION = "2";//通知
    public final static String PROMOTION = "3";//营销
    public static ExecutorService pool = Executors.newFixedThreadPool(16);
    private static final Logger logger = LoggerFactory.getLogger(YFSmsUtil.class);
    /**
     * 异步发送短信
     * @param phones 手机号（多个逗号分开）
     * @param content 内容
     * @param type YFSmsUtil.VERIFYCODE验证码 YFSmsUtil.NOTITION通知 YFSmsUtil.PROMOTION营销
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/3 20:21
     * @author: xieqiang
     */
    public static void sendAsyn(final String phones, final String content, final String type){

        pool.execute(new Runnable() {
            @Override
            public void run() {
                send(phones,content,type);
            }
        });

    }
    /**
     * 同步步发送短信
     * @param phones 手机号（多个逗号分开）
     * @param content 内容
     * @param type YFSmsUtil.VERIFYCODE验证码 YFSmsUtil.NOTITION通知 YFSmsUtil.PROMOTION营销
     * @return
     * 返回值
     * <code>
     *
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/4 10:49
     * @author: xieqiang
     */
    public static SmsResult send(String phones, String content, String type){
        SmsResult result = new SmsResult();
        try{

            logger.info("YFsendSms params=|"+phones+"content="+content);
//            if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
//                    Constants.INVELOMENT_TYPE_TEST)) {
//                result.setSucc(true);
//                result.setResultStr("test");
//                return result;
//            }


            String key = "";
            String secret = "";
            switch(type){
                default:
                    break;
                case VERIFYCODE:
                    key = appKey;
                    secret = appSecret;
                    break;
                case NOTITION:
                    key = appKey;
                    secret = appSecret;
                    break;
                case PROMOTION:
                    key = appKeyPromotion;
                    secret = appSecretPromotion;
                    break;

            }
            String params = "appKey="+key+"&appSecret="+secret+"&phones="+phones+"&content=【爱上街】"+content;
            String reqResult = HttpUtil.doHttpPost(YFSmsUtil.URL, params);
            logger.info(StringUtil.appendStrs("sendSms params=|", phones, "|", content, "|", reqResult));

            JSONObject json = JSON.parseObject(reqResult);
            if ("000000".equals(json.getString("errorCode"))) {
                result.setSucc(true);
                result.setResultStr(json.getString("errorMsg"));
            } else {
                result.setSucc(false);
                result.setResultStr(json.getString("errorMsg"));
            }
            return result;
        }catch (Exception e){
            logger.info("云锋短息发送error:",e);
        }
        result.setSucc(false);
        result.setResultStr("发送失败");
        return result;
    }
    /*public static void main(String[] args){
        //YFSmsUtil.send("13613317541","我是谁",YFSmsUtil.NOTITION);
        SmsUtil.switchSmsSend("15634981699","我是谁");
        System.out.print("ok");
    }*/
}
