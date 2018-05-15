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
//	String urlBase = "https://testapi.51fanbei.com";
	String urlBase = "http://localhost:8080";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
//	String userName = "15669066271";	//田建成 cardId:3111464125 支付密码123456
	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
//	String userName = "13370127054";	//王卿 	支付密码123456
//	String userName = "13656648524";	//朱玲玲 支付密码123456
//	String userName = "13510301615";	//王绪武 支付密码123456
//	String userName = "17756648524";	//新账号 支付密码123456
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	/**
	 * 获取借钱首页详情
	 */
	@Test
	public void getHomeInfo() {
		String url = urlBase + "/h5/loan/getLoanHomeInfo";
		testH5(url, null, userName, true);
	}
	
	/**
	 * 获取所有借钱记录，包含白领贷和小额贷记录
	 */
	@Test
	public void getAllBorrowList() {
		String url = urlBase + "/borrow/loanShop";
		Map<String,String> params = new HashMap<>();
		params.put("systemType", "1");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 贷款前确认
	 */
	@Test
	public void confirmLoan() {
		String url = urlBase + "/h5/loan/confirmLoan";
		Map<String,String> params = new HashMap<>();
		params.put("prdType", "BLD_LOAN");
		params.put("amount", 1000+"");
		params.put("periods", 1+"");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 发起贷款申请
	 */
	@Test
	public void applyLoan() {
		String url = urlBase + "/h5/loan/applyLoan";
		Map<String,String> params = new HashMap<>();
		params.put("prdType", "BLD_LOAN");
		params.put("amount", 6000+"");
		params.put("periods", 2+"");
		
		params.put("remark", "白领贷借款");
		params.put("loanRemark", "装修");
		params.put("repayRemark", "工资");
		params.put("payPwd", DigestUtils.md5Hex("123456")); // 支付密码，根据测试账号需要替换！
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

	/**
	 * 贷款申请成功后，模拟 UPS 回调 返呗API
	 */
	@Test
	public void delegatePay() {
		String url = urlBase + "/third/ups/delegatePay?";
		String orderNo = "01dpay23425234dfssdfs";
		String merPriv = UserAccountLogType.LOAN.getCode();
		String tradeState = "00";
		String reqExt = "154";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeState=" + tradeState +"&reqExt="+reqExt;
		url += reqStr;
		
		testH5(url, null, userName ,true);
	}

	/**
	 * 获取白领贷协议
	 */
	@Test
	public void getWhiteLoanProtocol() {
		String url = urlBase + "/h5/loan/getWhiteLoanProtocol";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 53+"");//1038.66
		params.put("totalServiceFee", "30.27");
		params.put("loanRemark", "0");
		params.put("repayRemark", "0");
		params.put("interestRate", "0");
		params.put("serviceRate", "0");
		params.put("overdueRate", "0");
		params.put("amount", "1000");
		params.put("nper", "3");
		testH5(url, params, userName, true);
	}

	/**
	 * 按期还款
	 */
	@Test
	public void repayDo() {
		String url = urlBase + "/loan/loanRepayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", 500+"");//351.27
		params.put("couponId", "0");
		params.put("rebateAmount", "0");
		
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464853");
		params.put("actualAmount",1000+"");
		params.put("loanId", 586+"");
		params.put("loanPeriodsIds", "1007,1008");
		
		testApi(url, params, userName, true);
	}
	
	/**
	 * 提前结清还款
	 */
	@Test
	public void allRepayDo() {
		String url = urlBase + "/loan/loanAllRepayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", 675.41+"");
		params.put("couponId", "0");
		params.put("rebateAmount", "0");
		
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464125");
		params.put("actualAmount", 675.41+"");
		params.put("loanId", 207+"");
		
		testApi(url, params, userName, true);
	}
	
	/**
	 * 获取贷款详情
	 */
	@Test
	public void getLoanInfo() {
		String url = urlBase + "/h5/loan/getLoanInfo";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 583+"");
		
		testH5(url, params, userName, true);
	}
	
	/**
	 * 获取贷款还款计划
	 */
	@Test
	public void loanRepayPlan() {
		String url = urlBase + "/h5/loan/loanRepayPlan";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 189+"");
		
		testH5(url, params, userName, true);
	}
	
	@Test
	public void getLoanRepayments() {
		String url = urlBase + "/h5/loan/getLoanRepayments";
		Map<String,String> params = new HashMap<>();
		params.put("loanId", 9+"");
		
		testH5(url, params, userName, true);
	}
	
	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "hq2018051510143800802";
		String merPriv = PayOrderSource.REPAY_LOAN.getCode();
		String tradeNo = "xianFenghq2018051510143800802";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName, true);
	}
	
	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		
	}
	
}
