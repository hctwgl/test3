package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
	
    public static JSONObject getUserInfo(String appid, String secret, String code) {
	String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
	JSONObject access_token = httpsRequest(url, "POST", null);

	logger.info("WxUtil.getOpenidByCode:" + JSON.toJSONString(access_token));
	// 获取refresh_token
	String refreshToken = (String) access_token.get("refresh_token");

	url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appid + "&grant_type=refresh_token&refresh_token=" + refreshToken;
	JSONObject refresh_token = httpsRequest(url, "POST", null);

	logger.info("WxUtil.getOpenidByCode:" + JSON.toJSONString(refresh_token));

	// 获取用户信息
	String openid = (String) refresh_token.get("openid");
	String accessToken = (String) refresh_token.get("access_token");
	url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
	JSONObject userInfo = httpsRequest(url, "GET", null);

	return userInfo;
    }

    /**
     * 发送https请求
     * 
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     * @param outputStr
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    private static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
	JSONObject jsonObject = null;
	try {
	    // 创建SSLContext对象，并使用我们指定的信任管理器初始化
	    TrustManager[] tm = { new MyX509TrustManager() };
	    SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
	    sslContext.init(null, tm, new java.security.SecureRandom());
	    // 从上述SSLContext对象中得到SSLSocketFactory对象
	    SSLSocketFactory ssf = sslContext.getSocketFactory();

	    URL url = new URL(requestUrl);
	    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	    conn.setSSLSocketFactory(ssf);

	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    // 设置请求方式（GET/POST）
	    conn.setRequestMethod(requestMethod);

	    // 当outputStr不为null时向输出流写数据
	    if (null != outputStr) {
		OutputStream outputStream = conn.getOutputStream();
		// 注意编码格式
		outputStream.write(outputStr.getBytes("UTF-8"));
		outputStream.close();
	    }

	    // 从输入流读取返回内容
	    InputStream inputStream = conn.getInputStream();
	    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	    String str = null;
	    StringBuffer buffer = new StringBuffer();
	    while ((str = bufferedReader.readLine()) != null) {
		buffer.append(str);
	    }

	    // 释放资源
	    bufferedReader.close();
	    inputStreamReader.close();
	    inputStream.close();
	    inputStream = null;
	    conn.disconnect();
	    jsonObject = JSONObject.parseObject(buffer.toString());
	} catch (ConnectException ce) {
	    System.out.println(ce);
	} catch (Exception e) {
	    System.out.println(e);
	}
	return jsonObject;
    }
}  

class MyX509TrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	    // TODO Auto-generated method stub

	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	    // TODO Auto-generated method stub

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
	    // TODO Auto-generated method stub
	    return null;
	}

}
