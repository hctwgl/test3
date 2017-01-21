package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 *@类描述：铜盾工具类
 *@author 陈金虎 2017年1月21日 下午8:16:24
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
public class TongdunUtil extends AbstractThird {
	private static String partnerCode = null;
	private static String partnerKey = null;
	private static String appName = null;
	
	/**
	 * 借贷申请
	 * 
	 * @param idNumber
	 * @param realName
	 * @param mobile
	 * @param email
	 * @return
	 */
	public static String applyPreloan(String idNumber,String realName,String mobile,String email){
		String urlPre = "https://api.tongdun.cn/preloan/apply/v5?";
		String reqUrl = urlPre + "partner_code=" + getPatnerCode() + "&partner_key=" + getPartnerKey() + "&app_name=" + getAppName();
		String query = "id_number=" + idNumber + "&name=" + realName + "&mobile=" + mobile + "&email=" + email;
		String reqResult = HttpUtil.doHttpPost(reqUrl, query);
		logger.info(StringUtil.appendStrs("applyPreloan params=|",idNumber,"|",realName,"|",mobile,"|",email,"|,reqResult=",reqResult));
		
		if(StringUtil.isBlank(reqResult)){
			return "";
		}
		JSONObject jo = JSONObject.parseObject(reqResult);
		if(jo.getBooleanValue("success")){
			return jo.getString("report_id");
		}
		return "";
	}
	
	/**
	 * 查询借贷申请结果
	 * @param reportId
	 * @return
	 */
	public static JSONObject queryPreloan(String reportId){
		String urlPre = "https://api.tongdun.cn/preloan/report/v7?";
		String reqUrl = urlPre + "partner_code=" + getPatnerCode() + "&partner_key=" + getPartnerKey() + "&app_name=" + getAppName() + "&report_id=" + reportId;
		String reqResult = HttpUtil.doGet(reqUrl, 1000);
		logger.info(StringUtil.appendStrs("queryPreloan params=|",reportId,"|,reqResult=",reqResult));
		if(StringUtil.isBlank(reqResult)){
			return null;
		}
		JSONObject jo = JSONObject.parseObject(reqResult);
		return jo;
	}
	
	
	
	
	
	
	
	
	
	private static String getPatnerCode(){
		if(partnerCode == null){
			partnerCode = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TONGDUN_PARTNER_CODE), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return partnerCode;
	}
	
	private static String getPartnerKey(){
		if(partnerKey == null){
			partnerKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TONGDUN_PARTNER_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return partnerKey;
	}
	
	private static String getAppName(){
		if(appName == null){
			appName = ConfigProperties.get(Constants.CONFKEY_TONGDUN_APP_NAME);
		}
		return appName;
	}
	
}
