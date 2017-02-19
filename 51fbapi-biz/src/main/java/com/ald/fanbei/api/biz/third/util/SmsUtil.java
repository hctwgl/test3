package com.ald.fanbei.api.biz.third.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：发送短信工具类
 * @author 陈金虎 2017年2月7日 下午8:49:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("smsUtil")
public class SmsUtil extends AbstractThird {

	private final static String URL = "http://www.dh3t.com/json/sms/Submit";
	private final static String ACCOUNT = "dh15433";
	private final static String SIGN = "【51返呗】";
	private static String password = null;
	private static String REGIST_TEMPLATE = "注册验证码为:&param1";
	private static String FORGET_TEMPLATE = "证码为:&param1";
	private static String BIND_TEMPLATE = "证码为:&param1";
	private static String SETPAY_TEMPLATE = "证码为:&param1";

	
	private static String TEST_VERIFY_CODE = "888888";
	
	
	@Resource
	AfSmsRecordService afSmsRecordService;

	/**
	 * 发送注册短信验证码
	 * 
	 * @param mobile
	 * @param content
	 */
	public boolean sendRegistVerifyCode(String mobile) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("invalid mobile", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = REGIST_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.REGIST, mobile, verifyCode, 0l, smsResult);
		return smsResult.isSucc();
	}
	
	/**
	 * 忘记密码发送短信验证码
	 * @param mobile 用户绑定的手机号（注意：不是userName）
	 * @param userId 用户id
	 * @return
	 */
	public boolean sendForgetPwdVerifyCode(String mobile,Long userId){
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("invalid mobile", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = FORGET_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.FORGET_PASS, mobile, verifyCode, 0l, smsResult);
		return smsResult.isSucc();
	}
	/**
	 * 绑定手机发送短信验证码
	 * @param mobile 用户绑定的手机号（注意：不是userName）
	 * @param userId 用户id
	 * @return
	 */
	public boolean sendMobileBindVerifyCode(String mobile,Long userId){
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("invalid mobile", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = BIND_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.MOBILE_BIND, mobile, verifyCode, userId, smsResult);
		return smsResult.isSucc();
	}
	/**
	 * 设置支付发送短信验证码
	 * @param mobile 用户绑定的手机号（注意：不是userName）
	 * @param userId 用户id
	 * @return
	 */
	public boolean sendSetPayPwdVerifyCode(String mobile,Long userId){
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("invalid mobile", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = SETPAY_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.SET_PAY_PWD, mobile, verifyCode, userId, smsResult);
		return smsResult.isSucc();
	}
	/**
	 * 对单个手机号发送普通短信
	 * 
	 * @param mobile
	 *            手机号
	 * @param content
	 *            短信内容
	 */
	public void sendSms(String mobile, String content) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("invalid mobile", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		sendSmsToDhst(mobile, content);
	}
	
	/**
	 * 验证短信验证码
	 * @param mobile
	 * @param verifyCode
	 * @param type
	 */
	public void checkSmsByMobileAndType(String mobile, String verifyCode, SmsType type) {

		AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, type.getCode());

		if (smsDo == null) {
			throw new FanbeiException("invalid Sms", FanbeiExceptionCode.USER_REGIST_SMS_NOTEXIST);

		}

		// 判断验证码是否一致
		String realCode = smsDo.getVerifyCode();
		if (!StringUtils.equals(verifyCode, realCode)) {
			throw new FanbeiException("invalid Sms", FanbeiExceptionCode.USER_REGIST_SMS_ERROR);

		}
		// 判断验证码是否过期
		if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
			throw new FanbeiException("invalid Sms", FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);

		}
		// 更新为已经验证
		afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
	}

	/**
	 * 对多个手机号发送普通短信短信
	 * 
	 * @param mobiles
	 *            手机号列表
	 * @param content
	 *            短信内容
	 */
	public void sendSms(List<String> mobiles, String content) {
		if (CollectionUtil.isEmpty(mobiles) || mobiles.size() > 500) {
			throw new FanbeiException("mobile count error", FanbeiExceptionCode.SMS_MOBILE_COUNT_TOO_MANAY);
		}
		sendSmsToDhst(StringUtil.turnListToStr(mobiles), content);
	}

	/**
	 * 对单个手机号发送短消息，这里不验证手机号码有效性
	 * 
	 * @param mobile
	 * @param msg
	 */
	private static SmsResult sendSmsToDhst(String mobiles, String content) {
		SmsResult result = new SmsResult();
		if(!StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)){
			result.setSucc(true);
			result.setResultStr("test");
			return result;
		}
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("account", ACCOUNT);
		paramsMap.put("password", DigestUtil.MD5(getPassword()).toLowerCase());
		paramsMap.put("phones", mobiles);
		paramsMap.put("content", content);
		paramsMap.put("sign", SIGN);
		String reqResult = HttpUtil.doHttpPost(URL,JSONObject.toJSONString(paramsMap));

		logger.info(StringUtil.appendStrs("sendSms params=|", mobiles, "|",content, "|", reqResult));
		return result;
	}
	

	/**
	 * 短信记录表中增加记录
	 * @param smsType
	 * @param mobile
	 * @param verifyCode
	 * @param userId
	 * @param smsResult
	 * @return
	 */
	private int addSmsRecord(SmsType smsType,String mobile,String verifyCode,Long userId,SmsResult smsResult){
		if(!StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)){
			verifyCode = TEST_VERIFY_CODE;
		}
		AfSmsRecordDo recordDo = new AfSmsRecordDo();
		recordDo.setMobile(mobile);
		recordDo.setUserId(userId);
		recordDo.setType(smsType.getCode());
		recordDo.setVerifyCode(verifyCode);
		recordDo.setResult(smsResult.getResultStr());
		return afSmsRecordService.addSmsRecord(recordDo);
	}

	private static String getPassword() {
		if (password == null) {
			password = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_DHST_PASSWORD),ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return password;
	}
}

class SmsResult {
	private boolean isSucc;
	private String resultStr;

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}

	public String getResultStr() {
		return resultStr;
	}

	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}

}