package com.ald.fanbei.web.test.api.loan;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class LoanTest  extends BaseTest{
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
	
//	@Test
	public void getHomeInfo() {
		String url = urlBase + "/h5/loan/getLoanHomeInfo";
		testH5(url, null, userName, true);
	}
	
//	@Test
	public void confirmLoan() {
		String url = urlBase + "/h5/loan/confirmLoan";
		Map<String,String> params = new HashMap<>();
		params.put("prdType", "BLD_LOAN");
		params.put("amount", 50000+"");
		params.put("periods", 4+"");
		testH5(url, params, userName, true);
	}
	
	@Test
	public void applyLoan() {
		String url = urlBase + "/h5/loan/applyLoan";
		Map<String,String> params = new HashMap<>();
		params.put("prdType", "BLD_LOAN");
		params.put("amount", 50000+"");
		params.put("periods", 4+"");
		
		params.put("remark", "白领贷借款");
		params.put("loanRemark", "装修");
		params.put("repayRemark", "工资");
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("latitude", "20.35654");
		params.put("longitude", "21.65645");
		params.put("province", "浙江省");
		params.put("city", "杭州市");
		params.put("county", "中国");
		params.put("address", "滨江区星耀城1期");
		params.put("blackBox", "sadasd");
		params.put("bqsBlackBox", "asdasdasd");
		params.put("couponId", "");
		
		testH5(url, params, userName, true);
	}

//	@Test
	public void repayDo() {
		
	}
	
//	@Test
	public void  collect() {
		
	}
	
//	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		
	}
	
}
