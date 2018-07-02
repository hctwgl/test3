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

public class LegalBorrowTest  extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://localhost:8080";
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
		String url = urlBase + "/legalborrow/confirmLegalRenewalPay";
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

	@Test
	public void getCashPageType() {
		String url = urlBase + "/legalborrow/getCashPageType";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName ,true);
	}

//	@Test
	public void  repayDo() {
		String url = urlBase + "/legalborrow/repayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", "201");
		params.put("userCouponId", "");
		params.put("rebateAmount", "");
		params.put("payPwd", DigestUtils.md5Hex("111111"));
		params.put("cardId", "3111464124");
		params.put("actualAmount", "201");
		params.put("borrowOrderId", "243");
//		params.put("borrowId", "1259939");
		params.put("from", "ORDER");
		
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "xj2017122018091400239";
		String merPriv = PayOrderSource.RENEW_CASH_LEGAL.getCode();
		String tradeNo = "";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv +"&tradeNo="+tradeNo+"&tradeState="+tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		
//        String respCode = StringUtil.null2Str(request.getParameter("respCode"));
//        String respDesc = StringUtil.null2Str(request.getParameter("respDesc"));
//        String tradeDesc = StringUtil.null2Str(request.getParameter("tradeDesc"));
		
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		String url = urlBase + "/third/collection/offlineRepayment?";
		 
		String tradeNo = "offline" + System.currentTimeMillis();
		Map<String,String> params = new HashMap<>();
		params.put("repay_no", tradeNo);
		params.put("borrow_no", "jq2017121917075900521");
		params.put("repay_type", "BANK");
		params.put("repay_time", DateUtil.formatDateTime(new Date()));
		params.put("repay_amount", "20100.00");
		params.put("rest_amount", "10000.00");
		params.put("trade_no", tradeNo);
		params.put("is_balance", YesNoStatus.NO.getCode());
		
		String data = JsonUtil.toJSONString(params);
		String timestamp = DateUtil.getDateTimeFull(new Date());
		String sign = DigestUtil.MD5(data);
		String reqStr = "data=" + URLEncoder.encode(data, "UTF-8") + "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +"&sign="+URLEncoder.encode(sign, "UTF-8");
		url += reqStr;
		Map<String,String> paramsT = new HashMap<>();
		
		testApi(url, paramsT, userName ,true);
	}
	
}
