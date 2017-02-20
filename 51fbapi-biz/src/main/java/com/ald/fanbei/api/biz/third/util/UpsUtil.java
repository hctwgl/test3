package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.biz.bo.UpsAuthPayConfirmReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayConfirmRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthPayRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignValidReqBo;
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
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;


/**
 *@类现描述：支付路由,统一支付工具
 *@author chenjinhu 2017年2月18日 下午10:09:44
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsUtil extends AbstractThird {
	
	private static String url = "http://192.168.101.57:8091/ups/main.html";
	private static String notifyHost = "http://192.168.96.67";
	
	private static String SYS_KEY = "01";
	private static String TRADE_STATUE_SUCC = "00";
	private static String DEFAULT_CERT_TYPE = "01";  //默认证件类型：身份证
	private static String DEFAULT_BANK_CARD_TYPE = "10";//默认银行卡类型：储蓄卡
//	private static String PAY_CANAL_YSB      = "02";   //银生宝
	private static String PAY_CHANL_BF       = "07";   //报付
	
	
	//orderNo规则  4位业务码  + 4位接口码  + 11位身份标识（手机号或者身份证后11位） + 13位时间戳
	
	/**
	 * 单笔代付
	 * 
	 * @param amount 金额
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param purpose 用途
	 * @param notifyUrl 异步通知url
	 * @param clientType 客户端类型
	 */
	public static void delegatePay(BigDecimal amount,String realName,String cardNo,String purpose,String notifyUrl,String clientType){
		String orderNo = getOrderNo("dpay", cardNo.substring(cardNo.length()-8,cardNo.length()));
//		String orderNo = "dp"+cardNo.substring(cardNo.length()-15,cardNo.length()) + System.currentTimeMillis();
		UpsDelegatePayReqBo reqBo = new UpsDelegatePayReqBo();
		setPubParam(reqBo,"delegatePay",orderNo,clientType);
		reqBo.setAmount(amount.toString());
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setPurpose(purpose);
		reqBo.setNotifyUrl(notifyUrl);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsDelegatePayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsDelegatePayRespBo.class);
		logThird(authSignResp, "delegatePay", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 认证支付
	 * @param amount 支付金额
	 * @param userCustNo 第三方账户号  //TODO 
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param idNumber 身份证号
	 * @param notifyUrl 异步回调地址
	 * @param clientType 客户端类型
	 */
	public static void authPay(BigDecimal amount,String userCustNo,String realName,String cardNo,String idNumber,String notifyUrl,String clientType){
//		String orderNo = "ap"+cardNo.substring(cardNo.length()-15,cardNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apay", cardNo.substring(cardNo.length()-8,cardNo.length()));
		UpsAuthPayReqBo reqBo = new UpsAuthPayReqBo();
		setPubParam(reqBo,"authPay",orderNo,clientType);
		reqBo.setAmount(amount.toString());
		reqBo.setUserCustNo(userCustNo);
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setNotifyUrl(notifyUrl);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsAuthPayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayRespBo.class);
		logThird(authSignResp, "authPay", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 支付认证确认
	 * 
	 * @param smsCode 短信验证码
	 * @param tradeNo 原认证支付交易订单号
	 * @param notifyUrl 异步通知url
	 * @param clientType 客户端类型
	 */
	public static void authPayConfirm(String smsCode,String tradeNo,String notifyUrl,String clientType){
//		String orderNo = "apc"+tradeNo.substring(tradeNo.length()-14,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apco", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsAuthPayConfirmReqBo reqBo = new UpsAuthPayConfirmReqBo();
		setPubParam(reqBo,"authPayConfirm",orderNo,clientType);
		reqBo.setSmsCode(smsCode);
		reqBo.setTradeNo(tradeNo);
		reqBo.setNotifyUrl(notifyUrl);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsAuthPayConfirmRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayConfirmRespBo.class);
		logThird(authSignResp, "authPayConfirm", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 单笔交易查询
	 * 
	 * @param tradeType 交易类型
	 * @param tradeNo 交易订单号
	 * @param clientType 客户端类型
	 */
	public static void queryTrade(String tradeType,String tradeNo,String clientType){
//		String orderNo = "qt"+tradeNo.substring(tradeNo.length()-15,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("qtra", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsQueryTradeReqBo reqBo = new UpsQueryTradeReqBo();
		setPubParam(reqBo,"queryTrade",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setTradeType(tradeType);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsQueryTradeRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryTradeRespBo.class);
		logThird(authSignResp, "queryTrade", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 签约
	 */
	public static void authSign(String bankCode,String realName,String mobile,String idNumber,String cardNumber,String clientType){
//		String orderNo = "as"+idNumber.substring(idNumber.length()-15,idNumber.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("asig", mobile.substring(mobile.length()-8,mobile.length()));
		UpsAuthSignReqBo reqBo = new UpsAuthSignReqBo();
		setPubParam(reqBo,"authSign",orderNo,clientType);
		
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(mobile);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNumber);
		reqBo.setBankCardType(DEFAULT_BANK_CARD_TYPE);
		reqBo.setTradeDate(DateUtil.formatDate(new Date(), DateUtil.FULL_PATTERN));
		reqBo.setClientType(clientType);
		reqBo.setStartDate(DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN));
		reqBo.setEndDate(DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN));//TODO 多久
		reqBo.setReturnUrl(notifyHost + "/third/ups/authSignReturn");
		reqBo.setNotifyUrl(notifyHost + "/third/ups/authSignNotify");
		
		//与宝付签约
		reqBo.setPayCanal(PAY_CHANL_BF);
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.AUTH_UPS_BF_SIGN_ERROR);
		}
		
		
		UpsAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignRespBo.class);
		logThird(authSignResp, "authSign", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 签约短信验证
	 * @param tradeNo 交易号
	 * @param verifyCode 短信验证
	 * @param notifyUrl
	 * @param clientType
	 */
	public static void authSignValid(String tradeNo,String verifyCode,String notifyUrl,String clientType){
//		String orderNo = "asvd"+tradeNo.substring(tradeNo.length()-15,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("asva", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsAuthSignValidReqBo reqBo = new UpsAuthSignValidReqBo();
		setPubParam(reqBo,"authSignValid",orderNo,clientType);
		reqBo.setSmsCode(verifyCode);
		reqBo.setTradeNo(tradeNo);
		reqBo.setTradeDate(DateUtil.formatDate(new Date(), DateUtil.FULL_PATTERN));//TODO 当前时间 or 订单时间
		reqBo.setNotifyUrl(notifyUrl);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignRespBo.class);
		logThird(authSignResp, "authSignValid", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	public static void queryAuthSign(String tradeNo,String contractNo,String realName,String idNumber,String cardNo,String startDate,String endDate,String clientType){
//		String orderNo = "asv"+tradeNo.substring(tradeNo.length()-15,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("qasi", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsQueryAuthSignReqBo reqBo = new UpsQueryAuthSignReqBo();
		setPubParam(reqBo,"queryAuthSign",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setContractNo(contractNo);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNo);
		reqBo.setStartDate(startDate);
		reqBo.setEndDate(endDate);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsQueryAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryAuthSignRespBo.class);
		logThird(authSignResp, "queryAuthSign", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
		
	}
	
	public static void signDelay(String startDate,String endDate,String contractNo,String returnUrl,String notifyUrl,String remark,String clientType){
//		String orderNo = "sd"+contractNo.substring(contractNo.length()-15,contractNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("sdel", contractNo.substring(contractNo.length()-8,contractNo.length()));
		UpsSignDelayReqBo reqBo = new UpsSignDelayReqBo();
		setPubParam(reqBo,"signDelay",orderNo,clientType);
		reqBo.setStartDate(startDate);
		reqBo.setEndDate(endDate);
		reqBo.setContractNo(contractNo);
		reqBo.setReturnUrl(returnUrl);
		reqBo.setNotifyUrl(notifyUrl);
		reqBo.setRemark(remark);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsSignDelayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsSignDelayRespBo.class);
		logThird(authSignResp, "signDelay", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	public static void collect(BigDecimal amount,String phone,String purpose,String contractNo,String remark,String returnUrl,String notifyUrl,String clientType){
		String orderNo = getOrderNo("coll", contractNo.substring(contractNo.length()-8,contractNo.length()));
		UpsCollectReqBo reqBo = new UpsCollectReqBo();
		setPubParam(reqBo,"collect",orderNo,clientType);
		reqBo.setAmount(amount.toString());
		reqBo.setPhone(phone);
		reqBo.setPurpose(purpose);
		reqBo.setContractNo(contractNo);
		reqBo.setRemark(remark);
		reqBo.setReturnUrl(returnUrl);
		reqBo.setNotifyUrl(notifyUrl);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		logThird(authSignResp, "signDelay", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
		
	}
	
	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 * @param identity 身份标识（固定8位）
	 */
	private static String getOrderNo(String method,String identity){
		if(StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity) || identity.length() != 8){
			throw new FanbeiException();//TODO 
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
//		payRoutBo.setPayCanal(payCanal);
		payRoutBo.setClientType(clientType);
		payRoutBo.setMerPriv("");
		payRoutBo.setReqExt("");
	}
	
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(new Date().getTime());
	}
}
