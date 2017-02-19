package com.ald.fanbei.api.biz.third.util;

import java.util.Date;

import com.ald.fanbei.api.biz.bo.PayRoutAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.PayRoutAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.PayRoutReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;


/**
 *@类现描述：支付路由工具
 *@author chenjinhu 2017年2月18日 下午10:09:44
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class PayRoutUtil extends AbstractThird {
	
	private static String url = "http://192.168.101.57:8091/ups/main.html";
	
	/**
	 * 签约
	 */
	public static void authSign(String bankCode,String realName,String mobile,String idNumber,String cardNumber,String clientType){
		PayRoutAuthSignReqBo uathSign = new PayRoutAuthSignReqBo();
		setPubParam(uathSign);
		uathSign.setService("authSign");
		uathSign.setOrderNo("as"+idNumber.substring(idNumber.length()-15,idNumber.length()) + System.currentTimeMillis());
		uathSign.setClientType(clientType);
		
		uathSign.setBankCode(bankCode);
		uathSign.setRealName(realName);
		uathSign.setPhone(mobile);
		uathSign.setCertType("01");
		uathSign.setCertNo(idNumber);
		uathSign.setCardNo(cardNumber);
		uathSign.setBankCardType("10");
		uathSign.setTradeDate(DateUtil.formatDate(new Date(), DateUtil.FULL_PATTERN));
		uathSign.setClientType(clientType);
		uathSign.setStartDate(DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN));
		uathSign.setEndDate(DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN));//TODO 多久
		uathSign.setReturnUrl("");//TODO 同步还是异步
		uathSign.setNotifyUrl("");//TODO 同步还是异步
		
		String reqResult = HttpUtil.httpPost(url, uathSign);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException();//TODO
		}
		PayRoutAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,PayRoutAuthSignRespBo.class);
		if(authSignResp != null && StringUtil.equals(authSignResp.getTradeState(), "00")){
			//succ
		}else{
			throw new FanbeiException();//TODO
		}
	}
	
	/**
	 * 短信验证码
	 * @param verifyCode
	 */
	public static void authSignValid(String tradeNo,String verifyCode){
		
	}
	
	
	
	
	private static void setPubParam(PayRoutReqBo payRoutBo){
		payRoutBo.setVersion("10");
		payRoutBo.setMerNo("");//TODO what?
//		payRoutBo.setOrderNo(orderNo);
//		payRoutBo.setPayCanal(payCanal);
//		payRoutBo.setClientType(clientType);
//		payRoutBo.setMerPriv(merPriv);
		payRoutBo.setReqExt("");
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}
}
