package com.ald.fanbei.api.web.third.controller;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * oauth2.0实现
 */
public class Oauth2Client {

    protected final static Logger logger = LoggerFactory.getLogger(Oauth2Client.class);
    /**
     * 微信获取openId的接口(含参数)
     */
    private static final String OAUTH2_WECHAT_OPENID_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid=AppID&secret=SECRET&grant_type=authorization_code&code=CODE";

    /**
     * 易宝收银台地址
     */
    private static final String YEEPAY_CASHIER_URL="https://cash.yeepay.com/newwap/wechatlow/request?";

    /**
     * 接受微信回调，获取openId，返回易宝收银台地址
     * @param request
     * @return
     */
    public static String oauthCallBack(HttpServletRequest request,String appId,String appSecret,String merchantNo){
        logger.info("gettoken in");
        //1,接受微信oauth2.0回调参数state、code
        String token = request.getParameter("state");
        String code = request.getParameter("code");
        //2,拼接微信oauth2.0获取openId的接口地址
        String openIdUrl = OAUTH2_WECHAT_OPENID_URL;
        openIdUrl = openIdUrl.replace("AppID", appId).replace("SECRET", appSecret).replace("CODE", code);
        logger.info("gettoken openurl:"+openIdUrl);
        //3,使用httpclient，调用微信获取openId的接口，获取openId
        String result = HttpClientUtil.doGet(openIdUrl, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openId = jsonObject.getString("openid");
        //4,拼接易宝收银台的地址，并返回
        String suffixUrl = "token=" + token + "&merchantNo=" + merchantNo + "&wpayId=" + openId;
        String yeepayCashierUrl = YEEPAY_CASHIER_URL+suffixUrl;
        logger.info("gttoken last:"+yeepayCashierUrl);
        return yeepayCashierUrl;
    }

}
