package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsAuthPayConfirmReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayConfirmRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignValidReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.bo.UpsBatchDelegatePayReqBo;
import com.ald.fanbei.api.biz.bo.UpsBatchDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectReqBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayReqBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.UpsQueryAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.UpsQueryAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.UpsQueryTradeReqBo;
import com.ald.fanbei.api.biz.bo.UpsQueryTradeRespBo;
import com.ald.fanbei.api.biz.bo.UpsReqBo;
import com.ald.fanbei.api.biz.bo.UpsSignDelayReqBo;
import com.ald.fanbei.api.biz.bo.UpsSignDelayRespBo;
import com.ald.fanbei.api.biz.bo.UpsSignReleaseReqBo;
import com.ald.fanbei.api.biz.bo.UpsSignReleaseRespBo;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxXMLParser;
import com.ald.fanbei.api.biz.service.wxpay.WxpayConfig;
import com.ald.fanbei.api.biz.service.wxpay.WxpayCore;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 *@类现描述：支付路由,统一支付工具
 *@author chenjinhu 2017年2月18日 下午10:09:44
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("upsUtil")
public class UpsUtil extends AbstractThird {
	
	private static String url = null;
//	private static String notifyHost = "http://192.168.96.67";
	private static String notifyHost = null;
	
	private static String SYS_KEY = "01";
	private static String TRADE_STATUE_SUCC = "00";
	private static String TRADE_STATUE_DEAL = "20";
	private static String TRADE_RESP_SUCC = "0000";

	private static String DEFAULT_CERT_TYPE = "01";  //默认证件类型：身份证
	private static String PAYMENT_FLAG = "00";
	private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
//	private static String DEFAULT_BANK_CARD_TYPE = "10";//默认银行卡类型：储蓄卡
//	private static String PAY_CANAL_YSB      = "02";   //银生宝
//	private static String PAY_CHANL_BF       = "07";   //报付
	
	//orderNo规则  4位业务码  + 4位接口码  + 11位身份标识（手机号或者身份证后11位） + 13位时间戳
	
	private static String getNotifyHost(){
		if(notifyHost==null){
			notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
			return notifyHost;
		}
		return notifyHost;
	}
	
	private static String getUpsUrl(){
		if(url==null){
			url = ConfigProperties.get(Constants.CONFKEY_UPS_URL);
			return url;
		}
		return url;
	}
	
	/**
	 * 单笔代付
	 * 
	 * @param amount 金额
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param userNo 用户在商户的唯一标识
	 * @param phone 用户在商户的唯一标识
	 * @param bankName 银行名称
	 * @param bankCode 银行编号
	 * @param purpose 用途
	 * @param notifyUrl 异步通知url
	 * @param clientType 客户端类型
	 */
	public static UpsDelegatePayRespBo delegatePay(BigDecimal amount,String realName,String cardNo,String userNo,
			String phone,String bankName,String bankCode,String purpose,String clientType,String merPriv,String reqExt){
		amount = setActualAmount(amount);
		String orderNo = getOrderNo("dpay", phone.substring(phone.length()-4,phone.length()));
		UpsDelegatePayReqBo reqBo = new UpsDelegatePayReqBo();
		setPubParam(reqBo,"delegatePay",orderNo,clientType);
		reqBo.setMerPriv(merPriv);
		reqBo.setReqExt(reqExt);
		reqBo.setAmount(amount.toString());
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setUserNo(userNo);
		reqBo.setPhone(phone);
		reqBo.setBankName(bankName);
		reqBo.setBankCode(bankCode);
		reqBo.setPurpose(purpose);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/delegatePay");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		try {
			String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
			logThird(reqResult, "delegatePay", reqBo);
			if(StringUtil.isBlank(reqResult)){
				UpsDelegatePayRespBo authSignResp = new UpsDelegatePayRespBo();
				authSignResp.setSuccess(false);
				return authSignResp;
			}
			UpsDelegatePayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsDelegatePayRespBo.class);
			if(authSignResp != null && authSignResp.getRespCode()!=null && StringUtil.equals(authSignResp.getRespCode(), TRADE_RESP_SUCC)){
				authSignResp.setSuccess(true);
				return authSignResp;
			}else{
				UpsDelegatePayRespBo authSignResp1 = new UpsDelegatePayRespBo();
				authSignResp1.setSuccess(false);
				return authSignResp1;
			}
		} catch (Exception e) {
			UpsDelegatePayRespBo authSignResp = new UpsDelegatePayRespBo();
			authSignResp.setSuccess(false);
			return authSignResp;
		}
	}
	
	/**
	 * 认证支付
	 * @param amount 支付金额
	 * @param userNo 第三方唯一标识
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param idNumber 身份证号
	 * @param notifyUrl 异步回调地址
	 * @param clientType 客户端类型
	 */
	public static UpsAuthPayRespBo authPay(BigDecimal amount,String userNo,String realName,String cardNo,String idNumber,String clientType,String clientIp){
		amount = setActualAmount(amount);
//		String orderNo = "ap"+cardNo.substring(cardNo.length()-15,cardNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apay", cardNo.substring(cardNo.length()-4,cardNo.length()));
		UpsAuthPayReqBo reqBo = new UpsAuthPayReqBo();
		setPubParam(reqBo,"authPay",orderNo,clientType);
		JSONObject obj = new JSONObject();
		obj.put("clientIp", clientIp);
		reqBo.setReqExt(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setAmount(amount.toString());
		reqBo.setUserNo(userNo);
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authPay");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "authPay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_ERROR);
		}
		UpsAuthPayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
	}
	
	/**
	 * 
	 * 支付认证确认
	 * @param payOrderNo 认证支付返回的tradeNo
	 * @param cardNo 银行卡号码
	 * @param userNo 第三方唯一标识
	 * @param smsCode 短信验证码
	 * @param tradeNo 原认证支付交易订单号
	 * @param notifyUrl 异步通知url
	 * @param clientType 客户端类型
	 */
	public static UpsAuthPayConfirmRespBo authPayConfirm(String payOrderNo,String cardNo,String userNo,
			String smsCode,String tradeNo,String clientType){
//		String orderNo = "apc"+tradeNo.substring(tradeNo.length()-14,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apco", tradeNo.substring(tradeNo.length()-4,tradeNo.length()));
		UpsAuthPayConfirmReqBo reqBo = new UpsAuthPayConfirmReqBo();
		setPubParam(reqBo,"authPayConfirm",orderNo,clientType);
		JSONObject obj = new JSONObject();
		obj.put("authPayOrderNo", payOrderNo);
		reqBo.setReqExt(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setSmsCode(smsCode);
		reqBo.setCardNo(cardNo);
		reqBo.setUserNo(userNo);
		reqBo.setTradeNo(tradeNo);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authPayConfirm");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "authPayConfirm", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_CONFIRM_ERROR);
		}
		UpsAuthPayConfirmRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayConfirmRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
	}
	
	/**
	 * 单笔交易查询
	 * 
	 * @param tradeType 交易类型
	 * @param tradeNo 交易订单号
	 * @param clientType 客户端类型
	 */
	public static UpsQueryTradeRespBo queryTrade(String tradeType,String tradeNo,String clientType){
//		String orderNo = "qt"+tradeNo.substring(tradeNo.length()-15,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("qtra", tradeNo.substring(tradeNo.length()-4,tradeNo.length()));
		UpsQueryTradeReqBo reqBo = new UpsQueryTradeReqBo();
		setPubParam(reqBo,"queryTrade",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setTradeType(tradeType);
		
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "queryTrade", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_TRADE_ERROR);
		}
		UpsQueryTradeRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryTradeRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
	}
	
	/**
	 * 签约(包含四要素验证)
	 * 
	 * @param realName
	 * @param mobile
	 * @param idNumber
	 * @param cardNumber
	 * @param clientType
	 * @return
	 */
	public static UpsAuthSignRespBo authSign(String userNo,String realName,String mobile,String idNumber,String cardNumber,String clientType,String bankCode){
//		String orderNo = "as"+idNumber.substring(idNumber.length()-15,idNumber.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("sign", mobile.substring(mobile.length()-4,mobile.length()));
		UpsAuthSignReqBo reqBo = new UpsAuthSignReqBo();
		setPubParam(reqBo,"authSign",orderNo,clientType);
		reqBo.setUserNo(userNo);
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(mobile);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNumber);
		reqBo.setReturnUrl(getNotifyHost() + "/third/ups/authSignReturn");
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authSignNotify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "authSign", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		
		UpsAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignRespBo.class);
		logThird(authSignResp, "authSign", reqBo);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	/**
	 * 签约短信验证
	 * @param tradeNo 交易号
	 * @param verifyCode 短信验证
	 * @param notifyUrl
	 * @param clientType
	 */
	public static UpsAuthSignValidRespBo authSignValid(String userNo,String cardNo,String verifyCode,String clientType){
//		String orderNo = "asvd"+tradeNo.substring(tradeNo.length()-15,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("asva", cardNo.substring(cardNo.length()-4,cardNo.length()));
		UpsAuthSignValidReqBo reqBo = new UpsAuthSignValidReqBo();
		setPubParam(reqBo,"authSignValid",orderNo,clientType);
		reqBo.setUserNo(userNo);
		reqBo.setCardNo(cardNo);
		reqBo.setSmsCode(verifyCode);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authSignValidNotify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "authSignValid", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		UpsAuthSignValidRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignValidRespBo.class);
		logThird(authSignResp, "authSignValid", reqBo);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	public static UpsQueryAuthSignRespBo queryAuthSign(String tradeNo,String contractNo,String realName,String idNumber,String cardNo,String startDate,String endDate,String clientType){
		String orderNo = getOrderNo("qasi", tradeNo.substring(tradeNo.length()-4,tradeNo.length()));
		UpsQueryAuthSignReqBo reqBo = new UpsQueryAuthSignReqBo();
		setPubParam(reqBo,"queryAuthSign",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setContractNo(contractNo);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNo);
		reqBo.setStartDate(startDate);
		reqBo.setEndDate(endDate);
		
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "queryAuthSign", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_AUTH_SIGN_ERROR);
		}
		UpsQueryAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryAuthSignRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		
	}
	
	/**
	 * 协议延期
	 * @param startDate
	 * @param endDate
	 * @param contractNo
	 * @param returnUrl
	 * @param notifyUrl
	 * @param remark
	 * @param clientType
	 */
	public static UpsSignDelayRespBo signDelay(String startDate,String endDate,String contractNo,String returnUrl,String notifyUrl,String remark,String clientType){
		String orderNo = getOrderNo("sdel", contractNo.substring(contractNo.length()-4,contractNo.length()));
		UpsSignDelayReqBo reqBo = new UpsSignDelayReqBo();
		setPubParam(reqBo,"signDelay",orderNo,clientType);
		reqBo.setStartDate(startDate);
		reqBo.setEndDate(endDate);
		reqBo.setContractNo(contractNo);
		reqBo.setReturnUrl(returnUrl);
		reqBo.setNotifyUrl(notifyUrl);
		reqBo.setRemark(remark);
		
		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "signDelay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_SIGN_DELAY_ERROR);
		}
		UpsSignDelayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsSignDelayRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
	}
	
	/**
	 * 单笔代收
	 * 
	 * @param amount --交易金额
	 * @param userNo --用户唯一标识
	 * @param realName --真实姓名
	 * @param phone  --预留手机号
	 * @param bankCode --银行代码
	 * @param cardNo --银行卡号
	 * @param certNo --身份证号
	 * @param purpose --用途
	 * @param remark --
	 * @param returnUrl
	 * @param notifyUrl
	 * @param clientType
	 */
	public static UpsCollectRespBo collect(String orderNo,BigDecimal amount,String userNo,String realName,String phone,String bankCode,
			String cardNo,String certNo,String purpose,String remark,String clientType,String merPriv){
		//String orderNo = getOrderNo("coll", cardNo.substring(cardNo.length()-4,cardNo.length()));
		amount = setActualAmount(amount);
		UpsCollectReqBo reqBo = new UpsCollectReqBo();
		setPubParam(reqBo,"collect",orderNo,clientType);
		reqBo.setMerPriv(merPriv);
		reqBo.setAmount(amount.toString());
		reqBo.setUserNo(userNo);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setBankCode(bankCode);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(certNo);
		reqBo.setPurpose(purpose);
		reqBo.setRemark(remark);
		reqBo.setReturnUrl("");
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/collect");
/*		reqBo.setRealName("王宝");宝付测试
		reqBo.setPhone("18066542211");
		reqBo.setBankCode("ABC");
		reqBo.setCardNo("6228480444455553333");
		reqBo.setUserNo("test88888");
		reqBo.setCertNo("320301198502169142");*/
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "collect", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_COLLECT_ERROR);
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		if(authSignResp != null && TRADE_RESP_SUCC.equals(authSignResp.getRespCode()) && (
				TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())||TRADE_STATUE_DEAL.equals(authSignResp.getTradeState()))){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		
	}
	
	/**
	 * 批量代付
	 * 
	 * @param amount --交易金额
	 * @param userName --用户名称
	 * @param totalCount --总量
	 * @param remark --备注
	 * @param paymentDetails --批量代付明细 tradeNo amount certNo bankName
	 * @param returnUrl
	 * @param notifyUrl
	 * @param clientType
	 */
	public static UpsBatchDelegatePayRespBo batchDelegatePay(BigDecimal amount,String userName,String totalCount,String remark,String paymentDetails,String clientType){
		String orderNo = getOrderNo("badp", userName.substring(userName.length()-4,userName.length()));
		amount = setActualAmount(amount);
		UpsBatchDelegatePayReqBo reqBo = new UpsBatchDelegatePayReqBo();
		setPubParam(reqBo,"batchDelegatePay",orderNo,clientType);
		reqBo.setTotalAmount(amount.toString());
		reqBo.setTotalCount(totalCount);
		reqBo.setPaymentFlag(PAYMENT_FLAG);
		reqBo.setRemark(remark);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/batchDelegatePay");
		reqBo.setPaymentDetails(paymentDetails);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "batchDelegatePay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_BATCH_DELEGATE_ERR);
		}
		UpsBatchDelegatePayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsBatchDelegatePayRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null  && TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		
	}
	
	public static UpsSignReleaseRespBo signRelease(String userNo,String bankCode,String realName,String phone,
			String certNo,String cardNo,String clientType){
		String orderNo = getOrderNo("sire", phone.substring(phone.length()-4,phone.length()));
		UpsSignReleaseReqBo reqBo = new UpsSignReleaseReqBo();
		setPubParam(reqBo,"signRelease",orderNo,clientType);
		reqBo.setUserNo(userNo);
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(certNo);
		reqBo.setCardNo(cardNo);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/signRelease");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String reqResult = HttpUtil.httpPost(getUpsUrl(), reqBo);
		logThird(reqResult, "signRelease", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.SIGN_RELEASE_ERROR);
		}
		UpsSignReleaseRespBo authSignResp = JSONObject.parseObject(reqResult,UpsSignReleaseRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null  && TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.SIGN_RELEASE_ERROR);
		}
	}
	
	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 * @param identity 身份标识（固定4位）
	 */
	private static String getOrderNo(String method,String identity){
		if(StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity) || identity.length() != 4){
			throw new FanbeiException(FanbeiExceptionCode.UPS_ORDERNO_BUILD_ERROR);
		}
		return StringUtil.appendStrs(SYS_KEY,method,identity,System.currentTimeMillis());
	}
	
	
	
	/**
	 * 设置公共参数
	 * @param payRoutBo
	 * @param service
	 * @param orderNo
	 * @param clientType
	 */
	private static void setPubParam(UpsReqBo payRoutBo,String service,String orderNo,String clientType){
		payRoutBo.setVersion("10");
		payRoutBo.setService(service);
		payRoutBo.setMerNo("01151209000");//TODO what?
		payRoutBo.setOrderNo(orderNo);
//		payRoutBo.setPayCanal(PAY_CHANL_BF);
		payRoutBo.setClientType(clientType);
		payRoutBo.setMerPriv("");
		payRoutBo.setReqExt("");
	}
	
	/**
	 * 微信支付退款
	 * @param out_refund_no --订单退款编号
	 * @param out_trade_no --订单号
	 * @param refund_fee --退款金额
	 * @param total_fee --总金额
	 * @return
	 * @throws Exception
	 */
	public static String wxRefund(String out_refund_no,String out_trade_no,BigDecimal refund_fee,BigDecimal total_fee) throws Exception {
    	
		String appId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String mchId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_MCHID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String certPath = ConfigProperties.get(Constants.CONFKEY_WX_CERTPATH);
    	
		 //元转换分
        BigDecimal hundred = new BigDecimal(100);
        //退款金额
        String order_refund_fee = refund_fee.multiply(hundred).intValue()+"";
        //总金额
        String order_total_fee = total_fee.multiply(hundred).intValue()+"";
       
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("appid", appId);
    	param.put("mch_id", mchId);
    	param.put("nonce_str", DigestUtil.MD5(UUID.randomUUID().toString()));
    	param.put("op_user_id", mchId);
    	param.put("out_refund_no", out_refund_no);
    	param.put("out_trade_no", out_trade_no);
    	param.put("refund_fee", order_refund_fee);
    	param.put("total_fee", order_total_fee);
    	
    	String sign = WxSignBase.byteToHex(WxSignBase.MD5Digest((WxpayCore.toQueryString(param)).getBytes(Constants.DEFAULT_ENCODE)));
    	param.put(WxpayConfig.KEY_SIGN, sign);
    	String buildStr = WxpayCore.buildXMLBody(param);
    	String result = WxpayCore.refundPost(WxpayConfig.WX_REFUND_API, buildStr,mchId,certPath);
    	Properties respPro = WxXMLParser.parseXML(result);
    	return respPro.getProperty("result_code");
	}
	
	//微信支付
	public static Map<String,Object> buildWxpayTradeOrder(String orderNo,Long userId,String goodsName,BigDecimal totalFee,String attach) {
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			//构建调用微信需要的参数
			Map<String,Object> orderData = WxpayCore.buildWxOrderParam(orderNo, goodsName, totalFee, getNotifyHost()+"/third/ups/wxpayNotify",attach);
			String url = WxpayConfig.WX_UNIFIEDORDER_API;
			String buildStr = WxpayCore.buildXMLBody(orderData);
			
			//请求微信，获取预支付流水号
			String respXml = WxpayCore.executePostXML(url, buildStr);
			Properties respPro = WxXMLParser.parseXML(respXml);

			logger.info("buildStr = " + buildStr);
			//分装返回客户端参数
			result.put("appid", WxpayConfig.WX_APP_ID);
			result.put("partnerid", orderData.get("mch_id")+"");
			result.put("prepayid", respPro.getProperty("prepay_id"));
			result.put("noncestr", respPro.getProperty("nonce_str"));
			result.put("timestamp", System.currentTimeMillis()/1000+"");
			result.put("package", "Sign=WXPay");
			
			StringBuilder sbPre = new StringBuilder().append(WxpayCore.toQueryString(result));
			String signPre = WxSignBase.byteToHex(WxSignBase.MD5Digest(sbPre.toString().getBytes("utf-8")));
			result.put("sign", signPre.toUpperCase());
			result.put("wxpackage", "Sign=WXPay");
		}catch(Exception e){
			throw new FanbeiException("",FanbeiExceptionCode.REQ_WXPAY_ERR);
		}
		return result;
	}
	
	private static BigDecimal setActualAmount(BigDecimal amount){
		if(!StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)){
			amount = new BigDecimal("0.01").setScale(2);
		}else{
			amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		return amount;
	}
	

	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            prestr = prestr+value;
            /*if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }*/
        }

        return prestr;
    }
    
}
