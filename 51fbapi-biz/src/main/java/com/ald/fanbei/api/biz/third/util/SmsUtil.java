package com.ald.fanbei.api.biz.third.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
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
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.alibaba.fastjson.JSON;
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
	private final static String MARKETING_ACCOUNT = "dh15434";
	private final static String MARKETING_ACCOUNT_PASSWORD = "aSZqA6Ub";
	
	private final static String SIGN = "【51返呗】";
	private static String password = null;
	private static String REGIST_TEMPLATE = "注册验证码为:&param1;您正在注册51返呗，请在30分钟内完成注册";
	private static String FORGET_TEMPLATE = "验证码为:&param1;您正在找回51返呗的账户密码，请在30分钟内完成";
	private static String BIND_TEMPLATE = "验证码为:&param1;您正在51返呗绑定手机号，请在30分钟内完成";
	private static String SETPAY_TEMPLATE = "验证码为:&param1;您正在设置51返呗支付密码，请在30分钟内完成";
	private static String EMAIL_TEMPLATE = "验证码为:&param1;您正在设置51返呗更换绑定邮箱，请在30分钟内完成";
	private static String BORROWCASH_TEMPLATE = "您的借款审核通过，请留意您尾号&param1的银行卡资金变动，还款请使用51返呗app【任何索要银行卡号、要求存入现金的行为都是诈骗】";
	private static String GOODS_RESERVATION_SUCCESS = "恭喜你！预约成功！OPPOR11将于6月22日10点准时开售，提前0元预约购机享12期免息更有超级返利300元，有！ 且只在51返呗。回复td退订";
	private static String REGIST_SUCCESS_TEMPLATE = "认证送10元现金，借/还成功再抽现金，100%中奖，最高1888元，最低50元 http://t.cn/RI7CSL2 退订回T";

	private static String REPAY_BORROWCASH_SUCCESS_REMAINNOTREPAY = "成功还款&param1元，剩余待还金额&param2元。";
	private static String REPAY_BORROWCASH_SUCCESS_FINISH = "成功还款&param1元，该笔借钱已还完。";

	private static String TEST_VERIFY_CODE = "888888";

	// public static String sendUserName = "suweili@edspay.com";
	// public static String sendPassword = "Su272727";

	private static final String sendHostAddress = "smtp.mxhichina.com";// 发送邮件使用的服务器的地址

	@Resource
	AfSmsRecordService afSmsRecordService;
	
	@Resource
	AfResourceService afResourceService;
	
	

	/**
	 * 发送注册短信验证码
	 * 
	 * @param mobile
	 * @param content
	 */
	public boolean sendRegistVerifyCode(String mobile) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = REGIST_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.REGIST, mobile, verifyCode, 0l, smsResult);
		return smsResult.isSucc();
	}
	
	/**
	 * 借款成功发送短信提醒用户
	 * 
	 * @param mobile
	 * @param content
	 */
	public  boolean sendBorrowCashCode(String mobile,String bank) {
		String content = BORROWCASH_TEMPLATE.replace("&param1", bank);

		SmsResult smsResult = sendSmsToDhst(mobile, content);
		return smsResult.isSucc();
	}

	/**
     * 预约商品成功消息通知
     * @param mobile
     * @param goodsName
     * @param rsvNo
     * @return
     */
    public  boolean sendGoodsReservationSuccessMsg(String mobile,String content) {
    	if(StringUtil.isBlank(content)){
    		content = GOODS_RESERVATION_SUCCESS;
    	}
        SmsResult smsResult = sendMarketingSmsToDhst(mobile, content);
        return smsResult.isSucc();
    }
    
    
    /**
	 * 对单个手机号发送短消息，这里不验证手机号码有效性
	 * 
	 * @param mobile
	 * @param msg
	 */
	private static SmsResult sendMarketingSmsToDhst(String mobiles, String content) {
		SmsResult result = new SmsResult();
		if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
				Constants.INVELOMENT_TYPE_TEST)) {
			result.setSucc(true);
			result.setResultStr("test");
			return result;
		}
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("account", MARKETING_ACCOUNT);
		paramsMap.put("password", DigestUtil.MD5(MARKETING_ACCOUNT_PASSWORD).toLowerCase());
		paramsMap.put("phones", mobiles);
		paramsMap.put("content", content);
		paramsMap.put("sign", SIGN);
		String reqResult = HttpUtil.doHttpPost(URL, JSONObject.toJSONString(paramsMap));

		logger.info(StringUtil.appendStrs("sendSms params=|", mobiles, "|", content, "|", reqResult));
		
		JSONObject json = JSON.parseObject(reqResult);
		if(json.getInteger("result")==0){
			result.setSucc(true);
			result.setResultStr(json.getString("desc"));
		}else{
			result.setSucc(false);
			result.setResultStr(json.getString("desc"));
		}
		return result;
	}
	/**
	 * 忘记密码发送短信验证码
	 * 
	 * @param mobile
	 *            用户绑定的手机号（注意：不是userName）
	 * @param userId
	 *            用户id
	 * @return
	 */
	public boolean sendForgetPwdVerifyCode(String mobile, Long userId) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = FORGET_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.FORGET_PASS, mobile, verifyCode, 0l, smsResult);
		return smsResult.isSucc();
	}

	/**
	 * 绑定手机发送短信验证码
	 * 
	 * @param mobile
	 *            用户绑定的手机号（注意：不是userName）
	 * @param userId
	 *            用户id
	 * @return
	 */
	public boolean sendMobileBindVerifyCode(String mobile, Long userId) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = BIND_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.MOBILE_BIND, mobile, verifyCode, userId, smsResult);
		return smsResult.isSucc();
	}

	/**
	 * 设置支付发送短信验证码
	 * 
	 * @param mobile
	 *            用户绑定的手机号（注意：不是userName）
	 * @param userId
	 *            用户id
	 * @return
	 */
	public boolean sendSetPayPwdVerifyCode(String mobile, Long userId) {
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = SETPAY_TEMPLATE.replace("&param1", verifyCode);
		SmsResult smsResult = sendSmsToDhst(mobile, content);
		this.addSmsRecord(SmsType.SET_PAY_PWD, mobile, verifyCode, userId, smsResult);
		return smsResult.isSucc();
	}

	/**
	 * 设置邮箱验证码
	 * 
	 * @param mobile
	 *            用户绑定的手机号（注意：不是userName）
	 * @param userId
	 *            用户id
	 * @return
	 */
	public boolean sendEmailVerifyCode(String email, Long userId) {

		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = EMAIL_TEMPLATE.replace("&param1", verifyCode);
		SmsResult emailResult = new SmsResult();
		emailResult.setResultStr("email send");
		try {
			sendEmailToDhst(email, content);

			emailResult.setSucc(true);
			this.addEmailRecord(SmsType.EMAIL_BIND, email, verifyCode, userId, emailResult);
			return emailResult.isSucc();

		} catch (Exception e) {
			logger.error("sendEmailVerifyCode",e);
			emailResult.setSucc(false);
			return emailResult.isSucc();
		}finally{
			logger.info(StringUtil.appendStrs("sendEmail params=|", email, "|", content, "|", emailResult));
		}

	}

	/**
	 * 用户还款成功后发送短信提醒用户
	 * @param mobile
	 * @param repayMoney 还款金额
	 * @param notRepayMoney 剩余未还款金额
	 */
	public  boolean sendRepaymentBorrowCashWarnMsg(String mobile,String repayMoney,String notRepayMoney) {
		String content = REPAY_BORROWCASH_SUCCESS_FINISH.replace("&param1", repayMoney);
		if(StringUtil.isNotBlank(notRepayMoney)){
			content = REPAY_BORROWCASH_SUCCESS_REMAINNOTREPAY.replace("&param1", repayMoney).replace("&param2", notRepayMoney);
		}
		SmsResult smsResult = sendSmsToDhst(mobile, content);
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
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		sendSmsToDhst(mobile, content);
	}

	/**
	 * 验证短信验证码
	 * 
	 * @param mobile
	 * @param verifyCode
	 * @param type
	 */
	public void checkSmsByMobileAndType(String mobile, String verifyCode, SmsType type) {

		AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, type.getCode());
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(AfResourceType.CodeMaxFail.getCode());
		if (smsDo == null) {
			throw new FanbeiException("invalid Sms or email", FanbeiExceptionCode.USER_REGIST_SMS_NOTEXIST);
		}

		// 判断验证码是否一致
		String realCode = smsDo.getVerifyCode();
		if(resourceDo!=null){
			Integer fail = NumberUtil.objToIntDefault(resourceDo.getValue(), 6);
			if(fail<=smsDo.getFailCount()){
				throw new FanbeiException("invalid Sms or email fail max", FanbeiExceptionCode.USER_SMS_FAIL_MAX_ERROR);

			}
		}
		
		if (!StringUtils.equals(verifyCode, realCode)) {
			AfSmsRecordDo smsFailDo  = new AfSmsRecordDo();
			smsFailDo.setRid(smsDo.getRid());
			smsFailDo.setFailCount(1);
			afSmsRecordService.updateSmsFailCount(smsFailDo);
			throw new FanbeiException("invalid Sms or email fail", FanbeiExceptionCode.USER_REGIST_SMS_ERROR);
		}
		// 判断验证码是否过期
		if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
			throw new FanbeiException("invalid Sms or email", FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
		}
		if(smsDo.getIsCheck() == 1){
		
			throw new FanbeiException("invalid Sms or email", FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR);

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
	 * 注册成功,发送注册成功短信
	 * @param mobile
	 * @param content
	 */
	public void sendRegisterSuccessSms(String mobile){
		if (!CommonUtil.isMobile(mobile)) {
			throw new FanbeiException("无效手机号", FanbeiExceptionCode.SMS_MOBILE_NO_ERROR);
		}
		sendSmsToDhst(mobile, REGIST_SUCCESS_TEMPLATE);
	}

	/**
	 * 对单个手机号发送短消息，这里不验证手机号码有效性
	 * 
	 * @param mobile
	 * @param msg
	 */
	private static SmsResult sendSmsToDhst(String mobiles, String content) {
		SmsResult result = new SmsResult();
		if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
				Constants.INVELOMENT_TYPE_TEST)) {
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
		String reqResult = HttpUtil.doHttpPost(URL, JSONObject.toJSONString(paramsMap));

		logger.info(StringUtil.appendStrs("sendSms params=|", mobiles, "|", content, "|", reqResult));
		
		JSONObject json = JSON.parseObject(reqResult);
		if(json.getInteger("result")==0){
			result.setSucc(true);
			result.setResultStr(json.getString("desc"));
		}else{
			result.setSucc(false);
			result.setResultStr(json.getString("desc"));
		}
		return result;
	}

	private static void sendEmailToDhst(String email, String content) throws Exception {

		// 1. 创建参数配置, 用于连接邮件服务器的参数配置
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.smtp.auth", "true"); 
		props.setProperty("mail.smtp.host", sendHostAddress); // 发件人的邮箱的 SMTP 服务器地址
		
		
		props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
		props.setProperty("mail.smtp.socketFactory.fallback", "false");  
		props.setProperty("mail.smtp.socketFactory.port", "465");  
		
		// 2. 根据配置创建会话对象, 用于和邮件服务器交互
		Session session = Session.getDefaultInstance(props);
		session.setDebug(true); // 设置为debug模式, 可以查看详细的发送 log
		String sendUserName = AesUtil.decrypt(ConfigProperties.get(Constants.EMAIL_SEND_USERNAME),
				ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String sendPassword = AesUtil.decrypt(ConfigProperties.get(Constants.EMAIL_SEND_PWD),
				ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		// 3. 创建一封邮件
		MimeMessage message = createMimeMessage(session, sendUserName, email, content);

		// 4. 根据 Session 获取邮件传输对象
		Transport transport = session.getTransport();

		// 5. 使用 邮箱账号 和 密码 连接邮件服务器
		// 这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
		transport.connect(sendUserName, sendPassword);

		// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人,
		// 抄送人, 密送人
		transport.sendMessage(message, message.getAllRecipients());

		// 7. 关闭连接
		transport.close();

	}

	public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String content)
			throws Exception {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);

		// 2. From: 发件人
		message.setFrom(new InternetAddress(sendMail, "51返呗", "UTF-8"));

		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "", "UTF-8"));
		message.setSubject("51返呗邮箱验证吗", "UTF-8");
		message.setContent(content, "text/html;charset=UTF-8");
		message.setSentDate(new Date());

		// 7. 保存设置
		message.saveChanges();

		return message;
	}

	/**
	 * 短信记录表中增加记录
	 * 
	 * @param smsType
	 * @param mobile
	 * @param verifyCode
	 * @param userId
	 * @param smsResult
	 * @return
	 */
	private int addSmsRecord(SmsType smsType, String mobile, String verifyCode, Long userId, SmsResult smsResult) {
		if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
				Constants.INVELOMENT_TYPE_TEST)) {
			verifyCode = TEST_VERIFY_CODE;
		}
		AfSmsRecordDo recordDo = new AfSmsRecordDo();
		recordDo.setSendAccount(mobile);
		recordDo.setUserId(userId);
		recordDo.setType(smsType.getCode());
		recordDo.setVerifyCode(verifyCode);
		recordDo.setResult(smsResult.getResultStr());
		return afSmsRecordService.addSmsRecord(recordDo);
	}

	/**
	 * 短信记录表中增加记录
	 * 
	 * @param smsType
	 * @param mobile
	 * @param verifyCode
	 * @param userId
	 * @param smsResult
	 * @return
	 */
	private int addEmailRecord(SmsType smsType, String mobile, String verifyCode, Long userId, SmsResult smsResult) {

		AfSmsRecordDo recordDo = new AfSmsRecordDo();
		recordDo.setSendAccount(mobile);
		recordDo.setUserId(userId);
		recordDo.setType(smsType.getCode());
		recordDo.setVerifyCode(verifyCode);
		recordDo.setResult(smsResult.getResultStr());
		return afSmsRecordService.addSmsRecord(recordDo);
	}

	private static String getPassword() {
		if (password == null) {
			password = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_DHST_PASSWORD),
					ConfigProperties.get(Constants.CONFKEY_AES_KEY));
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