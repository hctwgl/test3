package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * 
 *@类描述：发送短信工具类
 *@author 陈金虎 2017年2月7日 下午8:49:23
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SmsUtil extends AbstractThird{
	
	private final static String HOST 					= "ws.montnets.com";
	private final static String PORT 					= "9002";
	private final static String SEND_SMS_API 			= "/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";
	
	private static final String PARAM_KEY_USERID 		= "userId";
	private static final String PARAM_KEY_PASSWORD 		= "password";
	private static final String PARAM_KEY_PSMOBIS 		= "pszMobis";
	private static final String PARAM_KEY_PSMSG 		= "pszMsg";
	private static final String PARAM_KEY_IMOBICOUNT	= "iMobiCount";
	private static final String PARAM_KEY_PSZSUBPORT	= "pszSubPort";
	
	/**
	 * 对单个手机号发送短消息，这里不验证手机号码有效性
	 * @param mobile
	 * @param msg
	 */
	public static void sendSms(String mobile,String msg){
		String url = StringUtil.appendStrs("http://",HOST,":",PORT,SEND_SMS_API);
		Map<String, String> paramsMap = getCommonParamMap();
		paramsMap.put(PARAM_KEY_PSMOBIS, mobile);
		paramsMap.put(PARAM_KEY_PSMSG, msg);
		paramsMap.put(PARAM_KEY_IMOBICOUNT, "1");
		String reqResult = HttpUtil.httpPost(url, paramsMap);

		logger.info(StringUtil.appendStrs("sendSms params=|",mobile,"|",msg,"|",reqResult));
	}
	
	/**
	 * 对多个手机号发送段消息,最多发送100个，不验证手机号码的有效性
	 * @param mobiles
	 * @param msg
	 */
	public static void sendSms(List<String> mobiles,String msg){
		if(mobiles.size() > 100){
			logger.error("mobiles is to mach,mobiles=" + mobiles);
			return;
		}
		String url = StringUtil.appendStrs("http://",HOST,":",PORT,SEND_SMS_API);
		Map<String, String> paramsMap = getCommonParamMap();
		paramsMap.put(PARAM_KEY_PSMOBIS, StringUtil.turnListToStr(mobiles, ","));
		paramsMap.put(PARAM_KEY_PSMSG, msg);
		paramsMap.put(PARAM_KEY_IMOBICOUNT, mobiles.size()+"");
		String reqResult = HttpUtil.httpPost(url, paramsMap);

		logger.info(StringUtil.appendStrs("sendSms params=|",mobiles,"|",msg,"|",reqResult));
		
	}
	
	private static Map<String, String> getCommonParamMap(){
		String userId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_USERID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String password = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_PASSWORD), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(PARAM_KEY_USERID, userId);
		paramsMap.put(PARAM_KEY_PASSWORD, password);
		paramsMap.put(PARAM_KEY_PSZSUBPORT, "*");
		return paramsMap;
	}
}