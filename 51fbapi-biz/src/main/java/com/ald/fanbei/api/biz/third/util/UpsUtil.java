package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
	
	private static String url = "http://60.190.230.35:52637/ups/main.html";
//	private static String notifyHost = "http://192.168.96.67";
	private static String notifyHost = "http://testapp.51fanbei.com";
	
	private static String SYS_KEY = "01";
	private static String TRADE_STATUE_SUCC = "00";
	private static String DEFAULT_CERT_TYPE = "01";  //默认证件类型：身份证
//	private static String DEFAULT_BANK_CARD_TYPE = "10";//默认银行卡类型：储蓄卡
//	private static String PAY_CANAL_YSB      = "02";   //银生宝
//	private static String PAY_CHANL_BF       = "07";   //报付
	
	
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
	public static UpsDelegatePayRespBo delegatePay(BigDecimal amount,String realName,String cardNo,String purpose,String clientType){
		amount = setActualAmount(amount);
		String orderNo = getOrderNo("dpay", cardNo.substring(cardNo.length()-8,cardNo.length()));
//		String orderNo = "dp"+cardNo.substring(cardNo.length()-15,cardNo.length()) + System.currentTimeMillis();
		UpsDelegatePayReqBo reqBo = new UpsDelegatePayReqBo();
		setPubParam(reqBo,"delegatePay",orderNo,clientType);
		reqBo.setAmount(amount.toString());
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setPurpose(purpose);
		reqBo.setNotifyUrl(notifyHost + "/third/ups/delegatePay");
		reqBo.setSignInfo(createLinkString(reqBo));

		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "delegatePay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_DELEGATE_PAY_ERROR);
		}
		UpsDelegatePayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsDelegatePayRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_DELEGATE_PAY_ERROR);
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
	public static UpsAuthPayRespBo authPay(BigDecimal amount,String userCustNo,String realName,String cardNo,String idNumber,String clientType,String clientIp){
		amount = setActualAmount(amount);
//		String orderNo = "ap"+cardNo.substring(cardNo.length()-15,cardNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apay", cardNo.substring(cardNo.length()-8,cardNo.length()));
		UpsAuthPayReqBo reqBo = new UpsAuthPayReqBo();
		setPubParam(reqBo,"authPay",orderNo,clientType);
		JSONObject obj = new JSONObject();
		obj.put("clientIp", clientIp);
		reqBo.setReqExt(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setAmount(amount.toString());
		reqBo.setUserCustNo(userCustNo);
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setNotifyUrl(notifyHost + "/third/ups/authPay");
		reqBo.setSignInfo(createLinkString(reqBo));
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "authPay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_ERROR);
		}
		UpsAuthPayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_ERROR);
		}
	}
	
	/**
	 * 
	 * 支付认证确认
	 * @param smsCode 短信验证码
	 * @param tradeNo 原认证支付交易订单号
	 * @param notifyUrl 异步通知url
	 * @param clientType 客户端类型
	 */
	public static UpsAuthPayConfirmRespBo authPayConfirm(String payOrderNo,String smsCode,String tradeNo,String clientType){
//		String orderNo = "apc"+tradeNo.substring(tradeNo.length()-14,tradeNo.length()) + System.currentTimeMillis();
		String orderNo = getOrderNo("apco", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsAuthPayConfirmReqBo reqBo = new UpsAuthPayConfirmReqBo();
		setPubParam(reqBo,"authPayConfirm",orderNo,clientType);
		reqBo.setSmsCode(smsCode);
		reqBo.setTradeNo(tradeNo);
		reqBo.setNotifyUrl(notifyHost + "/third/ups/authPayConfirm");
		reqBo.setSignInfo(createLinkString(reqBo));
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "authPayConfirm", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_CONFIRM_ERROR);
		}
		UpsAuthPayConfirmRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthPayConfirmRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_PAY_CONFIRM_ERROR);
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
		String orderNo = getOrderNo("qtra", tradeNo.substring(tradeNo.length()-8,tradeNo.length()));
		UpsQueryTradeReqBo reqBo = new UpsQueryTradeReqBo();
		setPubParam(reqBo,"queryTrade",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setTradeType(tradeType);
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "queryTrade", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_TRADE_ERROR);
		}
		UpsQueryTradeRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryTradeRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_TRADE_ERROR);
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
		String orderNo = getOrderNo("sign", mobile.substring(mobile.length()-8,mobile.length()));
		UpsAuthSignReqBo reqBo = new UpsAuthSignReqBo();
		setPubParam(reqBo,"authSign",orderNo,clientType);
		reqBo.setUserNo(userNo);
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(mobile);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNumber);
		reqBo.setReturnUrl(notifyHost + "/third/ups/authSignReturn");
		reqBo.setNotifyUrl(notifyHost + "/third/ups/authSignNotify");
		reqBo.setSignInfo(createLinkString(reqBo));
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "authSign", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		
		UpsAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignRespBo.class);
		logThird(authSignResp, "authSign", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
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
		String orderNo = getOrderNo("asva", cardNo.substring(cardNo.length()-8,cardNo.length()));
		UpsAuthSignValidReqBo reqBo = new UpsAuthSignValidReqBo();
		setPubParam(reqBo,"authSignValid",orderNo,clientType);
		reqBo.setUserNo(userNo);
		reqBo.setCardNo(cardNo);
		reqBo.setSmsCode(verifyCode);
		reqBo.setNotifyUrl(notifyHost + "/third/ups/authSignValidNotify");
		reqBo.setSignInfo(createLinkString(reqBo));
		
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "authSignValid", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		UpsAuthSignValidRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignValidRespBo.class);
		logThird(authSignResp, "authSignValid", reqBo);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	public static UpsQueryAuthSignRespBo queryAuthSign(String tradeNo,String contractNo,String realName,String idNumber,String cardNo,String startDate,String endDate,String clientType){
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
		logThird(reqResult, "queryAuthSign", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_AUTH_SIGN_ERROR);
		}
		UpsQueryAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryAuthSignRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_QUERY_AUTH_SIGN_ERROR);
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
		logThird(reqResult, "signDelay", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_SIGN_DELAY_ERROR);
		}
		UpsSignDelayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsSignDelayRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_SIGN_DELAY_ERROR);
		}
	}
	
	/**
	 * 单笔代收
	 * 
	 * @param amount
	 * @param phone
	 * @param purpose
	 * @param contractNo
	 * @param remark
	 * @param returnUrl
	 * @param notifyUrl
	 * @param clientType
	 */
	public static UpsCollectRespBo collect(BigDecimal amount,String phone,String purpose,String contractNo,String remark,String returnUrl,String notifyUrl,String clientType){
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
		logThird(reqResult, "collect", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.UPS_COLLECT_ERROR);
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.UPS_COLLECT_ERROR);
		}
		
	}
	
	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 * @param identity 身份标识（固定8位）
	 */
	private static String getOrderNo(String method,String identity){
		if(StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity) || identity.length() != 8){
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
		String certPath = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_CERTPATH), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
    	
		 //元转换分
        BigDecimal hundred = new BigDecimal(100);
        //退款金额
        String order_refund_fee = refund_fee.multiply(hundred).setScale(0)+"";
        //总金额
        String order_total_fee = total_fee.multiply(hundred).setScale(0)+"";
       
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
			Map<String,Object> orderData = WxpayCore.buildWxOrderParam(orderNo, goodsName, totalFee, notifyHost+"/third/ups/wxpayNotify",attach);
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
		}
		return amount;
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(new Date().getTime());
	}
}
