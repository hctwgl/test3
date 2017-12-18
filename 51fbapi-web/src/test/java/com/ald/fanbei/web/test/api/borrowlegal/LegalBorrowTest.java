package com.ald.fanbei.web.test.api.borrowlegal;

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
	String urlBase = "http://btestapp.51fanbei.com";
	String userName = "13638668564";
	
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

//	@Test
	public void getCashPageType() {
		String url = urlBase + "/legalborrow/getCashPageType";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName ,true);
	}

//	@Test
	public void  repayDo() {
		String url = urlBase + "/legalborrow/repayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", "100");
		params.put("userCouponId", "");
		params.put("rebateAmount", "");
		params.put("payPwd", DigestUtils.md5Hex("111111"));
		params.put("cardId", "3111464124");
		params.put("actualAmount", "100");
		params.put("borrowId", "1259666");
		params.put("from", "INDEX");
		
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "xj2017121718213200951";
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
	
	@Test
	public void  offlineRepayment() {
		String url = urlBase + "/third/collection/offlineRepayment";
		 
		Map<String,String> params = new HashMap<>();
		params.put("repay_no", "offline" + System.currentTimeMillis());
		params.put("borrow_no", "offline" + System.currentTimeMillis());
		params.put("repay_type", "offline" + System.currentTimeMillis());
		params.put("repay_time", "offline" + System.currentTimeMillis());
		params.put("repay_amount", "100.00");
		params.put("rest_amount", "100.00");
		params.put("trade_no", "offline" + System.currentTimeMillis());
		params.put("is_balance", YesNoStatus.YES.getCode());
		
		String data = JsonUtil.toJSONString(params);
		String timestamp = DateUtil.getDateTimeFull(new Date());
		String sign = DigestUtil.MD5(data);
		String reqStr = "data=" + data + "&timestamp=" + timestamp +"&sign="+sign;
		url += reqStr;
		
		testApi(url, null, userName ,true);
	}
	
}
