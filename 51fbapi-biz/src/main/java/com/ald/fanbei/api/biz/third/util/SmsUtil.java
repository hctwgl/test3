package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.Map;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 *@类描述：发送短信工具类
 *@author 陈金虎 2017年2月7日 下午8:49:23
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SmsUtil extends AbstractThird{
	
	private final static String URL = "http://www.dh3t.com/json/sms/Submit";
	private final static String ACCOUNT = "dh15433";
	private final static String SIGN = "51返呗";
	private static String password = null;
	
	/**
	 * 对单个手机号发送短消息，这里不验证手机号码有效性
	 * @param mobile
	 * @param msg
	 */
	public static void sendSms(String mobiles,String content){
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("account", ACCOUNT);
		paramsMap.put("password", DigestUtil.MD5(getPassword()).toLowerCase() );
		paramsMap.put("phones", mobiles);
		paramsMap.put("content", content);
		paramsMap.put("sign", SIGN);
		String reqResult = HttpUtil.doHttpPost(URL, JSONObject.toJSONString(paramsMap));

		logger.info(StringUtil.appendStrs("sendSms params=|",mobiles,"|",content,"|",reqResult));
	}
	

	private static String getPassword(){
		if(password == null){
			password = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_DHST_PASSWORD), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return password;
	}
}