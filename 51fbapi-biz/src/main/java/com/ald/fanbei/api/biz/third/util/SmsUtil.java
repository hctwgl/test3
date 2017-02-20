package com.ald.fanbei.api.biz.third.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
	

	   public static String sendUserName = "suweili@edspay.com";
	    public static String sendPassword = "Su272727";
	  
	  private static final String sendProtocol = "25";// 发送邮件使用的端口 
	  
	  private static final String sendHostAddress = "smtp.mxhichina.com";// 发送邮件使用的服务器的地址 
	  
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
	 * 设置邮箱验证码
	 * @param mobile 用户绑定的手机号（注意：不是userName）
	 * @param userId 用户id
	 * @return
	 */
	public boolean sendEmailVerifyCode(String email,Long userId){

		String verifyCode = CommonUtil.getRandomNumber(6);
		String content = SETPAY_TEMPLATE.replace("&param1", verifyCode);
		SmsResult emailResult = new SmsResult();
		emailResult.setResultStr("email send");
		try {
			sendEmailToDhst(email, content);
			
			emailResult.setSucc(true);
			this.addEmailRecord(SmsType.EMAIL_BIND, email, verifyCode, userId, emailResult);
			
		} catch (Exception e) {
			emailResult.setSucc(false);
			logger.info(StringUtil.appendStrs("sendEmail params=|", email, "|",content, "|", emailResult));

		}
	
		return emailResult.isSucc();
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
	
	
	

	private static void sendEmailToDhst(String email, String content) throws Exception{
		// 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.host", sendHostAddress);        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 请求认证，参数名称与具体实现有关

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, sendUserName, email,content);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect(sendUserName, sendPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();

       
	}
	
	 public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String content) throws Exception {
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
		recordDo.setSendAccount(mobile);
		recordDo.setUserId(userId);
		recordDo.setType(smsType.getCode());
		recordDo.setVerifyCode(verifyCode);
		recordDo.setResult(smsResult.getResultStr());
		return afSmsRecordService.addSmsRecord(recordDo);
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
	private int addEmailRecord(SmsType smsType,String mobile,String verifyCode,Long userId,SmsResult smsResult){
	
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