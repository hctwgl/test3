package com.ald.fanbei.api.biz.third.util;


import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 云峰短信对接
 *
 * @author xieqiang
 * @create 2018-01-03 20:13
 **/
public class YSSmsUtil {
    private static final Logger logger = LoggerFactory.getLogger(YSSmsUtil.class);

    private final static String url = "http://www.yescloudtree.cn:28001/";
    public final static String VERIFYCODE_YS = "1";//验证码
    public final static String NOTITION_YS = "2";//通知
    public final static String PROMOTION_YS = "3";//营销
    public final static String UNKNOWN_YS = "4";//盲发

    public final static String VERIFYCODE_KEY_YS = "jkcryy";//验证码
    public final static String VERIFYCODE_SECRET_YS = "ACCAF9088DF0F16B210231CFED7BA009";//验证码
    public final static String NOTITION_KEY_YS = "jkcryy";//通知
    public final static String NOTITION_SECRET_YS = "ACCAF9088DF0F16B210231CFED7BA009";//通知
    public final static String PROMOTION_KEY_YS = "jkcryx";//营销
    public final static String PROMOTION_SECRET_YS = "7ac0c4874c1cc74b6064ce8e38313e9c";//营销
    public final static String UNKNOWN_KEY_YS = "hzlydk";//盲发
    public final static String UNKNOWN_SECRET_YS = "c24fbd51a1679da7af1e1da64810e0cd";//盲发

    /**
     * 云树同步短息
     * @param mobile
     * @param content
     * @param type
     */
    public static SmsResult send(String mobile, String content, String type){
        logger.info("YSsendSms params=|"+mobile+"content="+content);
        SmsResult result = new SmsResult();
        try {

            String key = PROMOTION_KEY_YS;
            String secret = PROMOTION_SECRET_YS;
            String typestr = "";
            switch(type){
                default:
                    break;
                case VERIFYCODE_YS :
                    key = VERIFYCODE_KEY_YS;
                    secret = VERIFYCODE_SECRET_YS;
                    typestr = "验证码";
                    break;
                case NOTITION_YS :
                    key = NOTITION_KEY_YS;
                    secret = NOTITION_SECRET_YS;
                    typestr = "通知";
                    break;
                case PROMOTION_YS :
                    key = PROMOTION_KEY_YS;
                    secret = PROMOTION_SECRET_YS;
                    typestr = "营销";
                    break;
                case UNKNOWN_YS :
                    key = UNKNOWN_KEY_YS;
                    secret = UNKNOWN_SECRET_YS;
                    break;
            }
            logger.info("yunshu mobile="+mobile+" ,message="+content);
            content = URLEncoder.encode(content,"UTF-8");
            content = Base64.encode(content.getBytes("UTF-8"));
            String param = "Action=sendsmsbase64&UserName="+key+"&Password="+secret+"&Mobile="+mobile+"&Message="+content;
            String reqResult = HttpUtil.doHttpPost(url,param);
            logger.info("yunshu mobile="+mobile+" ,result="+reqResult);
            if (reqResult != null){
                if (reqResult.startsWith("0")) {
                    result.setSucc(true);
                    result.setResultStr(reqResult);
                } else {
                    result.setSucc(false);
                    result.setResultStr(reqResult);
                }
            }

            return result;
        }catch (Exception e){
            logger.info("yunshu ",e);
        }
        return result;
    }


    /**
     * 云树异步发送
     * @param phones
     * @param content
     * @param type
     */
    public static void sendAsyn(final String phones, final String content, final String type){

        YFSmsUtil.pool.execute(new Runnable() {
            @Override
            public void run() {
                send(phones,content,type);
            }
        });

    }
}
