package com.ald.fanbei.web.test.api.loan;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.web.test.common.BaseTest;

public class LoanTest  extends BaseTest{
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
	
	@Test
	public void getHomeInfo() {
		String url = urlBase + "/h5/loan/getLoanHomeInfo";
		testH5(url, null, userName, true);
	}
	
//	@Test
	public void getBorrowList() {
		String url = urlBase + "/borrowCash/getAllBorrowList";
		Map<String,String> params = new HashMap<>();
		params.put("pageNo", 1+"");
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void confirmLoan() {
		String url = urlBase + "/h5/loan/confirmLoan";
		Map<String,String> params = new HashMap<>();
		params.put("prdType", "BLD_LOAN");
		params.put("amount", 50000+"");
		params.put("periods", 1+"");
		testH5(url, params, userName, true);
	}
	
//	@Test
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
	public void delegatePay() {
		String url = urlBase + "/third/ups/delegatePay?";
		String orderNo = "01dpay23425234dfssdfs";
		String merPriv = UserAccountLogType.LOAN.getCode();
		String tradeState = "00";
		String reqExt = "13";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeState=" + tradeState +"&reqExt="+reqExt;
		url += reqStr;
		
		testH5(url, null, userName ,true);
	}
	
//	@Test
	public void repayDo() {
		String url = urlBase + "/h5/loan/loanRepayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repayAmount", 12688.06+"");
		params.put("couponId", "0");
		params.put("rebateAmount", "0");
		
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464125");
		params.put("actualAmount", 12688.06+"");
		params.put("loanId", 9+"");
		params.put("loanPeriodsId", 26+"");
		
		testH5(url, params, userName, true);
	}
	
//	@Test
	public void allRepayDo() {
		String url = urlBase + "/h5/loan/loanAllRepayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repayAmount", 37837.61+"");
		params.put("couponId", "0");
		params.put("rebateAmount", "0");
		
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464125");
		params.put("actualAmount", 37837.61+"");
		params.put("loanId", 9+"");
		
		testH5(url, params, userName, true);
	}
	
//	@Test
	public void getLoanInfo() {
		String url = urlBase + "/h5/loan/getLoanInfo";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 7+"");
		
		testH5(url, params, userName, true);
	}
	
//	@Test
	public void loanRepayPlan() {
		String url = urlBase + "/h5/loan/loanRepayPlan";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 7+"");
		
		testH5(url, params, userName, true);
	}
	
//	@Test
	public void getLoanRepayments() {
		String url = urlBase + "/h5/loan/getLoanRepayments";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 9+"");
		
		testH5(url, params, userName, true);
	}
	
//	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "hq2018013114255212824";
		String merPriv = PayOrderSource.REPAY_LOAN.getCode();
		String tradeNo = "xianFenghq2018013114255212824";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		
	}
	
}
