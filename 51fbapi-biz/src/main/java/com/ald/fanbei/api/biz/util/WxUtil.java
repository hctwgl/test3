package com.ald.fanbei.api.biz.util;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @说明： 对接微信的工具类
 * @date 2017-9-27 17:04:53
 * @author qiaopan
 *
 */
@Component("wxUtil")
public class WxUtil {
	@Resource
	 BizCacheUtil bizCacheUtil;
	
	static BizCacheUtil bizCacheUtil2;
	
	@PostConstruct
	public void init(){
		bizCacheUtil2 = bizCacheUtil;
		
	}
    protected static Logger   logger           = LoggerFactory.getLogger(WxUtil.class);

	private static int timeout = 1000;
	
	private static String appId = null;
	private static String secret = null;
	
	//private static ApplicationContext context;
	
	public static String getWxAppId(){
		if(appId != null){
			return appId;
		}
		appId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return appId;
	}
	
	public static String getWxSecret(){
		if(secret != null){
			return secret;
		}
		secret = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return secret;
	}
	
	/**
	 * 获取access token
	 * @return
	 */
	public static String getAccessToken(String appId,String secret){
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +appId+"&secret=" +secret;
		String reqResult = HttpUtil.doGet(url, timeout);
		JSONObject resultObj = JSONObject.parseObject(reqResult);
		//加缓存，
		//BizCacheUtil bizCacheUtil = new BizCacheUtil();
		String result = resultObj.getString("access_token");
		bizCacheUtil2.saveObject(Constants.CACHKEY_WX_TOKEN_LOCK,result,Constants.SECOND_OF_AN_HOUR_INT);
		return result;
	}
	
	/**
	 * 获取jsapi ticket
	 * @return
	 */
            public static String getJsapiTicket(String appId, String secret) {
        	// BizCacheUtil bizCacheUtil = new BizCacheUtil();
        	
        	Object object = bizCacheUtil2.getObject(Constants.CACHKEY_WX_TOKEN_LOCK);
        	String accessToken = object == null ? null : object.toString();
        	String ticketKey = "WECHAT_" + appId + "_ticket";
        	if (StringUtil.isBlank(accessToken)) {
        	    accessToken = getAccessToken(appId, secret);
        
        	    String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        	    String reqResult = HttpUtil.doGet(url, timeout);
        	    JSONObject resultObj = JSONObject.parseObject(reqResult);
        	    bizCacheUtil2.saveObject(ticketKey, resultObj.getString("ticket"), Constants.SECOND_OF_AN_HOUR_INT);
        
        	} 
        	    return bizCacheUtil2.getObject(ticketKey).toString();
            }
            
	/**
	 * 通过code获取openid
	 * @param code 菜单页面code
	 * @return
	 */
	@Deprecated
	public static String getOpenidByCode(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + getWxAppId() + "&secret="
				+ getWxSecret() + "&code=" + code + "&grant_type=authorization_code";
		String result = HttpUtil.doGet(url, 1000);
		JSONObject resultJson = JSONObject.parseObject(result);
		String openid = resultJson.getString("openid");
		return openid;
	}
}
