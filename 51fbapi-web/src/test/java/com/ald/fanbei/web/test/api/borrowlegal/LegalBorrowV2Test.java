package com.ald.fanbei.web.test.api.borrowlegal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.web.test.common.BaseTest;

public class LegalBorrowV2Test  extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "https://btestapp.51fanbei.com";
	String userName = "15669066271";
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	
	public void borrow() {
		
	}
	
//	@Test
	public void renewal() {
		String url = urlBase + "/legalborrowV2/confirmLegalRenewalPay";
		Map<String,String> params = new HashMap<>();
		
		params.put("borrowId", "1259666");
		params.put("payPwd", DigestUtils.md5Hex("111111"));
		params.put("renewalAmount", "200");
		params.put("cardId", "3111464124");
		params.put("goodsId", "91630");
		params.put("deliveryUser", "啊阿斯顿");
		params.put("deliveryPhone", "18659876572");
		params.put("address", "江苏南京");
		
		testApi(url, params, userName ,true);
	}

//	@Test
	public void getCashPageType() {
		String url = urlBase + "/legalborrowV2/getCashPageType";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName ,true);
	}

//	@Test
	public void  repayDo() {
		String url = urlBase + "/legalborrowV2/repayDoV2";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", "201");
		params.put("userCouponId", null);
		params.put("rebateAmount", null);
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464125");
		params.put("actualAmount", "201");
		params.put("borrowId", "1260148");
		
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "hq2018010811542108319";
		String merPriv = PayOrderSource.REPAY_CASH_LEGAL_V2.getCode();
		String tradeNo = "xianfeng21231";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		
//        String respCode = StringUtil.null2Str(request.getParameter("respCode"));
//        String respDesc = StringUtil.null2Str(request.getParameter("respDesc"));
//        String tradeDesc = StringUtil.null2Str(request.getParameter("tradeDesc"));
		
		testApi(url, params, userName ,true);
	}
	
	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		String url = urlBase + "/third/collection/offlineRepayment?";
		 
		String tradeNo = "offline" + System.currentTimeMillis();
		Map<String,String> params = new HashMap<>();
		params.put("repay_no", tradeNo);
		params.put("borrow_no", "jq2017122020523500899");
		params.put("repay_type", "BANK");
		params.put("repay_time", DateUtil.formatDateTime(new Date()));
		params.put("repay_amount", "1000.00");
		params.put("rest_amount", "10000.00");
		params.put("repay_cardNum", "6568654646462113"); // 模拟催收则 注解掉
		params.put("operator", "测试");// 模拟催收则 注解掉
		params.put("trade_no", tradeNo);
		params.put("is_balance", YesNoStatus.NO.getCode());
		params.put("is_admin", "Y");// 模拟催收则 注解掉
		
		String data = JsonUtil.toJSONString(params);
		String timestamp = DateUtil.getDateTimeFull(new Date());
		String sign = DigestUtil.MD5(data);
		String reqStr = "data=" + URLEncoder.encode(data, "UTF-8") + "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +"&sign="+URLEncoder.encode(sign, "UTF-8");
		url += reqStr;
		Map<String,String> paramsT = new HashMap<>();
		
		testApi(url, paramsT, userName ,false);
	}
	
}
