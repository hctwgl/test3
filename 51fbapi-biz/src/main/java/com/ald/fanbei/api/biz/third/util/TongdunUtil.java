package com.ald.fanbei.api.biz.third.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TongdunResultBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.TongdunEventEnmu;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：铜盾工具类
 * @author 陈金虎 2017年1月21日 下午8:16:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tongdunUtil")
public class TongdunUtil extends AbstractThird {
	private static String host = null;
	private static String partnerCode = null;
	private static String partnerKey = null;
	private static String appName = null;

	@Resource
	AfResourceService afResourceService;

	/**
	 * 借贷申请
	 * 
	 * @param idNumber
	 * @param realName
	 * @param mobile
	 * @param email
	 * @return
	 */
	public static String applyPreloan(String idNumber, String realName, String mobile, String email) {
		String urlPre = getPartnerHost() + "/preloan/apply/v5?";
		String reqUrl = urlPre + "partner_code=" + getPatnerCode() + "&partner_key=" + getPartnerKey() + "&app_name=" + getAppName();
		String query = "id_number=" + idNumber + "&name=" + realName + "&mobile=" + mobile + "&email=" + email;
		String reqResult = HttpUtil.doHttpPost(reqUrl, query);
		logger.info(StringUtil.appendStrs("applyPreloan params=|", idNumber, "|", realName, "|", mobile, "|", email, "|,reqResult=", reqResult));

		if (StringUtil.isBlank(reqResult)) {
			return "";
		}
		JSONObject jo = JSONObject.parseObject(reqResult);
		if (jo.getBooleanValue("success")) {
			return jo.getString("report_id");
		}
		return "";
	}

	/**
	 * app首页激活操作
	 * @param requsetId
	 * @param blackBox
	 *            设备指纹
	 * @param ip
	 *            真实ip
	 * @param accountLogin
	 *            账户名
	 * @param accountMobile
	 *            账户绑定手机号
	 * @param accountEmail
	 *            账户绑定邮箱
	 * @param isLimitVest
	 *            是事限制马甲包激活处理
	 */
	public void activeOperate(HttpServletRequest request,String requsetId, String blackBox, String ip,String version, String accountLogin, String accountMobile, boolean isLimitVest) {
		TongdunEventEnmu tongdunEvent = requsetId.startsWith("i") ? TongdunEventEnmu.ACTIVATE_IOS : TongdunEventEnmu.ACTIVATE_ANDROID;

		String[] idInfos = requsetId.split("_");
		if(idInfos.length!=4){
			logger.error("app activeOperate error,requsetId is invalid,requsetId="+requsetId);
			return;
		}
		if(isLimitVest && Constants.FORMAL_APP_IDENTIFY.equals(idInfos[3])){
			logger.info("app activeOperate exit,not need this operate,requsetId="+requsetId);
			return;
		}
		Map<String, Object> params = getCommonParam(tongdunEvent, blackBox, ip, accountLogin, accountMobile);
		params.put("app_ver", version);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("app activeOperate tongdunEvent error", e);
			return;
		}
		if (apiResp != null) {
			maidianLog.info(StringUtil.appendStrs(
					"	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
 					"	", "h",
 					"	rmtIP=", CommonUtil.getIpAddr(request), 
 					"	userName=", idInfos[1], 
 					"	", 0, 
 					"	", request.getRequestURI(),
 					"	result=",apiResp.get("final_decision"), 
 					"	",DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), 
 					"	", "md", 
 					"	appType=", idInfos[0],
 					"	channelCode=", idInfos[3],
 					"	seq_id=", apiResp.get("seq_id"),
 					"	policy_name=", apiResp.get("policy_name"),
 					"	reqD=", tongdunEvent.getEventId(), 
 					"	resD=",apiResp.get("risk_type")));
		}
	}
	
	/**
	 * 查询借贷申请结果
	 * 
	 * @param reportId
	 * @return
	 */
	public static TongdunResultBo queryPreloan(String reportId) {
		String urlPre = getPartnerHost() + "/preloan/report/v7?";
		String reqUrl = urlPre + "partner_code=" + getPatnerCode() + "&partner_key=" + getPartnerKey() + "&app_name=" + getAppName() + "&report_id=" + reportId;
		String reqResult = HttpUtil.doGet(reqUrl, 1000);
		logger.info(StringUtil.appendStrs("queryPreloan params=|", reportId, "|,reqResult=", reqResult));
		if (StringUtil.isBlank(reqResult)) {
			return null;
		}
		TongdunResultBo resultBo = new TongdunResultBo();
		JSONObject jo = JSONObject.parseObject(reqResult);
		resultBo.setSuccess(jo.getBooleanValue("success"));
		resultBo.setReasonCode(jo.getString("reason_code"));
		resultBo.setResultStr(jo.toJSONString());
		if (resultBo.isSuccess()) {
			resultBo.setReportId(jo.getString("report_id"));
			resultBo.setFinalDecision(jo.getString("final_decision"));
			resultBo.setFinalScore(jo.getInteger("final_score"));
		}
		return resultBo;
	}

	public String invoke(Map<String, Object> params) {

		StringBuilder result = new StringBuilder();
		try {
			String apiUrl = getPartnerHost() + "/riskService/v1.1";
			URL url = new URL(apiUrl);
			// 组织请求参数
			StringBuilder postBody = new StringBuilder();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() == null)
					continue;
				postBody.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "utf-8")).append("&");
			}

			if (!params.isEmpty()) {
				postBody.deleteCharAt(postBody.length() - 1);
			}

			SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			// 设置长链接
			conn.setRequestProperty("Connection", "Keep-Alive");
			// 设置连接超时
			conn.setConnectTimeout(1000);
			// 设置读取超时，建议设置为500ms。若同时调用了信息核验服务，请与客户经理协商确认具体时间
			conn.setReadTimeout(500);
			// 提交参数
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.getOutputStream().write(postBody.toString().getBytes());
			conn.getOutputStream().flush();
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				logger.warn("[FraudApiInvoker] invoke failed, response status:" + responseCode);
				return null;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line).append("\n");
			}
			return result.toString().trim();
			// return JSON.parseObject(result.toString().trim(),
			// FraudApiResponse.class);
		} catch (Exception e) {
			logger.error("[FraudApiInvoker] invoke throw exception, details: " + e);
		} finally {
			logger.info("tdfqz params=" + params + ",response=" + result);
		}
		return null;
	}

	/**
	 * android或ios注册
	 * 
	 * @param tongdunEvent
	 *            同盾事件标识
	 * @param blackBox
	 *            设备指纹
	 * @param ip
	 *            真实ip
	 * @param accountLogin
	 *            账户名
	 * @param accountMobile
	 *            账户绑定手机号
	 * @param accountEmail
	 *            账户绑定邮箱
	 * @param remCode
	 *            推荐码
	 */
	public void getRegistResult(String requsetId, String blackBox, String ip, String accountLogin, String accountMobile, String accountEmail, String remCode, String source) {
		TongdunEventEnmu tongdunEvent = requsetId.startsWith("i") ? TongdunEventEnmu.REGISTER_IOS : TongdunEventEnmu.REGISTER_ANDROID;

		accountLogin = accountMobile;

		String registSwitch = resourceValueWhithType(AfResourceType.registTongdunSwitch.getCode());
		Map<String, Object> params = getCommonParam(tongdunEvent, blackBox, ip, accountLogin, accountMobile);
		params.put("account_email", accountEmail);
		params.put("rem_code", remCode);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getLoginWebResult", e);
			return;
		}
		if (StringUtil.isBlank(registSwitch) || "0".equals(registSwitch)) {// 验证开关关闭
			return;
		}
		if (apiResp != null && apiResp.get(" ") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：" + accountMobile + "的用户在app端注册的时候被拦截....同盾返回的code是...." + apiResp.get("final_decision"));
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR);

		}
	}

	/**
	 * 
	 * 验证登录
	 * 
	 * @param tongdunEvent
	 *            同盾事件标识
	 * @param blackBox
	 *            设备指纹
	 * @param ip
	 *            真实ip
	 * @param accountLogin
	 *            账户名
	 * @param accountMobile
	 *            账户绑定手机号
	 * @param loginState
	 *            登录成功状态 1：失败 0：成功
	 */
	public void getLoginResult(String requsetId, String blackBox, String ip, String accountMobile, String accountLogin, String loginState, String source) {
		TongdunEventEnmu tongdunEvent = requsetId.startsWith("i") ? TongdunEventEnmu.LOGIN_IOS : TongdunEventEnmu.LOGIN_ANDROID;
		accountLogin = accountMobile;
		Map<String, Object> params = getCommonParam(tongdunEvent, blackBox, ip, accountLogin, accountMobile);
		params.put("state", loginState);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getLoginResult", e);
			return;
		}
		String loginSwitch = resourceValueWhithType(AfResourceType.loginTongdunSwitch.getCode());

		if (StringUtil.isBlank(loginSwitch) || "0".equals(loginSwitch)) {// 验证开关关闭
			return;
		}

		if (apiResp != null && apiResp.get("final_decision") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：" + accountMobile + "的用户在验证登录的时候被拦截" + "....同盾返回的code是...." + apiResp.get("final_decision"));
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR);
		}
	}

	/**
	 * @方法说明：渠道推广验证
	 * @author huyang
	 * @param sessionId
	 * @param channleCode
	 * @param pointCode
	 * @param ip
	 */
	public void getPromotionResult(String sessionId, String channleCode, String pointCode, String ip, String accountMobile, String accountLogin, String source) {
		TongdunEventEnmu tongdunEvent = TongdunEventEnmu.REGISTER_WEB;

		accountLogin = accountMobile;
		Map<String, Object> params = getCommonWebParam(tongdunEvent, sessionId, ip, accountLogin, accountMobile);
		params.put("channleCode", channleCode);
		params.put("pointCode", pointCode);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getLoginResult", e);
			return;
		}
		String promotionSwitch = resourceValueWhithType(AfResourceType.promotionTongdunSwitch.getCode());
		if (StringUtil.isBlank(promotionSwitch) || "0".equals(promotionSwitch)) {// 验证开关关闭
			return;
		}
		if (apiResp != null && apiResp.get("final_decision") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：【" + accountMobile + "】的用户在验证渠道【"+channleCode+"】位置【"+pointCode+"】推广注册的时候被拦截" + "....同盾返回的code是...." + apiResp.get("final_decision"));
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR);
		}
	}

	/**
	 * 网页注册获取短信验证码
	 * @param sessionId
	 * @param channleCode
	 * @param pointCode
	 * @param ip
	 * @param accountMobile
	 * @param accountLogin
	 * @param source
	 */
	public void getPromotionSmsResult(String sessionId, String channleCode, String pointCode, String ip, String accountMobile, String accountLogin, String source) {
		TongdunEventEnmu tongdunEvent = TongdunEventEnmu.SMS_WEB;

		accountLogin = accountMobile;
		Map<String, Object> params = getCommonWebParam(tongdunEvent, sessionId, ip, accountLogin, accountMobile);
		params.put("channleCode", channleCode);
		params.put("pointCode", pointCode);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getLoginResult", e);
			return;
		}
		String promotionSwitch = resourceValueWhithType(AfResourceType.promotionTongdunSwitch.getCode());
		if (StringUtil.isBlank(promotionSwitch) || "0".equals(promotionSwitch)) {// 验证开关关闭
			return;
		}
		if (apiResp != null && apiResp.get("final_decision") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：【" + accountMobile + "】的用户在渠道【"+channleCode+"】位置【"+pointCode+"】网页注册获取验证码时候被拦截" + "....同盾返回的code是...." + apiResp.get("final_decision"));
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR);
		}
	}

	/**
	 * 
	 * 
	 * @param tongdunEvent
	 *            同盾事件标识
	 * @param blackBox
	 *            设备指纹
	 * @param ip
	 *            真实ip
	 * @param accountLogin
	 *            账户名
	 * @param accountMobile
	 *            账户绑定手机号
	 * @param idNumber
	 *            身份证号码
	 * @param accountEmail
	 *            账户邮箱
	 * @param items
	 *            订单项
	 */
	public void getTradeResult(String requsetId, String blackBox, String ip, String accountLogin, String accountMobile, String idNumber, String realName, String accountEmail,
			String items, String source) {
		TongdunEventEnmu tongdunEvent = requsetId.startsWith("i") ? TongdunEventEnmu.TRADE_IOS : TongdunEventEnmu.TRADE_ANDROID;
		accountLogin = accountMobile;
		String registSwitch = resourceValueWhithType(AfResourceType.tradeTongdunSwitch.getCode());

		Map<String, Object> params = getCommonParam(tongdunEvent, blackBox, ip, accountLogin, accountMobile);
		params.put("account_email", accountEmail);
		params.put("id_number", idNumber);
		params.put("account_name", realName);
		params.put("items", items);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getTradeResult", e);
			return;
		}
		if (StringUtil.isBlank(registSwitch) || "0".equals(registSwitch)) {// 验证开关关闭
			return;
		}
		if (apiResp != null && apiResp.get("final_decision") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：" + accountMobile + ".....的用户在app端进行时候被拦截....同盾返回的code是...." + apiResp.get("final_decision"));
			// throw new
			// BussinessException("您投资手机号存在安全风险，如有疑问请联系客服:400-135-3388");
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_TRADE_ERROR);

		}
	}

	/**
	 * 借钱时间
	 * 
	 * @param tongdunEvent
	 *            同盾事件标识
	 * @param blackBox
	 *            设备指纹
	 * @param ip
	 *            真实ip
	 * @param accountLogin
	 *            账户名
	 * @param accountMobile
	 *            账户绑定手机号
	 * @param idNumber
	 *            身份证号码
	 * @param accountEmail
	 *            账户邮箱
	 * @param items
	 *            订单项
	 */
	public void getBorrowCashResult(String requsetId, String blackBox, String ip, String accountLogin, String accountMobile, String idNumber, String realName, String accountEmail,
			String items, String source) {
		TongdunEventEnmu tongdunEvent = requsetId.startsWith("i") ? TongdunEventEnmu.LOAN_IOS : TongdunEventEnmu.LOAN_ANDROID;
		accountLogin = accountMobile;
		String registSwitch = resourceValueWhithType(AfResourceType.tradeTongdunSwitch.getCode());

		Map<String, Object> params = getCommonParam(tongdunEvent, blackBox, ip, accountLogin, accountMobile);
		params.put("account_email", accountEmail);
		params.put("id_number", idNumber);
		params.put("account_name", realName);
		params.put("items", items);
		JSONObject apiResp = null;
		try {
			String respStr = invoke(params);
			// this.addTdFraud(accountLogin, accountMobile,
			// tongdunEvent.getClientOperate(), ip, respStr, source);
			apiResp = JSONObject.parseObject(respStr);
		} catch (Exception e) {
			logger.error("getTradeResult", e);
			return;
		}
		if (StringUtil.isBlank(registSwitch) || "0".equals(registSwitch)) {// 验证开关关闭
			return;
		}
		if (apiResp != null && apiResp.get("final_decision") != null
				&& resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
			logger.info("手机号码为：" + accountMobile + ".....的用户在app端进行时候被拦截....同盾返回的code是...." + apiResp.get("final_decision"));
			// throw new
			// BussinessException("您投资手机号存在安全风险，如有疑问请联系客服:400-135-3388");
			throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_TRADE_ERROR);

		}
	}

	private String resourceValueWhithType(String type) {

		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(type);
		if (afResourceDo == null) {
			return "";
		} else {
			return afResourceDo.getValue();
		}

	}

	private Map<String, Object> getCommonParam(TongdunEventEnmu tongdunEvent, String blackBox, String ip, String accountLogin, String accountMobile) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partner_code", "alading");// 此处值填写您的合作方标识
		params.put("secret_key", tongdunEvent.getSecretKey());// 此处填写对应app密钥
		params.put("event_id", tongdunEvent.getEventId());// 此处填写策略集上的事件标识
		params.put("black_box", blackBox);// 此处填写移动端sdk采集到的信息black_box
		params.put("account_login", accountLogin);// 以下填写其他要传的参数，比如系统字段，扩展字段
		params.put("account_mobile", accountMobile);// 以下填写其他要传的参数，比如系统字段，扩展字段
		params.put("ip_address", ip);

		return params;
	}

	private Map<String, Object> getCommonWebParam(TongdunEventEnmu tongdunEvent, String tokenId, String ip, String accountLogin, String accountMobile) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partner_code", "alading");// 此处值填写您的合作方标识
		params.put("secret_key", tongdunEvent.getSecretKey());// 此处填写对应app密钥
		params.put("event_id", tongdunEvent.getEventId());// 此处填写策略集上的事件标识
		params.put("token_id", tokenId);// 此处填写移动端sdk采集到的信息black_box
		params.put("account_login", accountLogin);// 以下填写其他要传的参数，比如系统字段，扩展字段
		params.put("account_mobile", accountMobile);// 以下填写其他要传的参数，比如系统字段，扩展字段
		params.put("ip_address", ip);

		return params;
	}

	// private void addTdFraud(String userName, String userPhone, String type,
	// String ip, String result, String source) {
	// AfTdFraudDo tdFraud = new AfTdFraudDo();
	// tdFraud.setUserName(userName == null ? "" : userName + source);
	// tdFraud.setResult(result == null ? "" : result);
	// tdFraud.setType(type);
	// tdFraud.setUserPhone(userPhone == null ? "" : userPhone);
	// tdFraud.setIp(ip);
	// afTdFraudSerVice.addTdFraud(tdFraud);
	// }

	private static String getPartnerHost() {
		if (host == null) {
			host = "https://api.tongdun.cn";
		}
		return host;
	}

	private static String getPatnerCode() {
		if (partnerCode == null) {
			partnerCode = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TONGDUN_PARTNER_CODE), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return partnerCode;
	}

	private static String getPartnerKey() {
		if (partnerKey == null) {
			partnerKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TONGDUN_PARTNER_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return partnerKey;
	}

	private static String getAppName() {
		if (appName == null) {
			appName = ConfigProperties.get(Constants.CONFKEY_TONGDUN_APP_NAME);
		}
		return appName;
	}

}
