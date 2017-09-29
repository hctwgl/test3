package com.ald.fanbei.api.biz.service.wxpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @date 2017-9-27 17:04:53
 * @author qiaopan
 *
 */
public class WxUtil {
    protected static Logger   logger           = LoggerFactory.getLogger(WxUtil.class);

	private static int timeout = 1000;
	
	private static String appId = null;
	private static String secret = null;
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
	public static String getAccessToken(){
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + getWxAppId()+"&secret=" + getWxSecret();
		String reqResult = HttpUtil.doGet(url, timeout);
		JSONObject resultObj = JSONObject.parseObject(reqResult);
		return resultObj.getString("access_token");
	}
	
	/**
	 * 获取jsapi ticket
	 * @return
	 */
	public static String getJsapiTicket(){
		String accessToken = getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
		String reqResult = HttpUtil.doGet(url, timeout);
		JSONObject resultObj = JSONObject.parseObject(reqResult);
		return resultObj.getString("ticket");
	}
}
