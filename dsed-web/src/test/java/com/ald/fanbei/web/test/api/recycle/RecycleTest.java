package com.ald.fanbei.web.test.api.recycle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.ald.fanbei.web.test.common.BaseTest;

public class RecycleTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
//	String urlBase = "https://btestapp.51fanbei.com";
	String urlBase = "http://localhost:80";
	String userName = AccountOfTester.夏枫.mobile;
	
	@Before
	public void init(){
		super.init(userName);
	}
	
	/**
	 * 获取回收首页详情
	 */
	@Test
	public void getBorrowRecycleHomeInfoApi() {
		String url = urlBase + "/h5/recycle/borrowRecycleHome";
		testH5(url, null, userName, true);
	}

	/**
	 * 获取白领贷协议
	 */
	@Test
	public void getRecycleProtocol() {
		String url = urlBase + "/h5/recycle/getRecycleProtocol";
		Map<String,String> params = new HashMap<>();
		params.put("borrowId", 33399739+"");//1038.66
		params.put("riskDailyRate", "10");
		params.put("goodsModel", "128G");
		params.put("goodsName", "ipone70");
		params.put("type", "10");
		params.put("overdueRate", "0.10");
		params.put("interestRate", "0.06");
		params.put("amount", "1000");
		testH5(url, params, userName, true);
	}

	/**	"borrowTag": "OVERDUE_RATE"

	 * 获取回收记录
	 */
	@Test
	public void borrowRecycleRecordApi() {
		String url = urlBase + "/h5/recycle/borrowRecycleRecord";
		Map<String,String> params = new HashMap<>();
		params.put("start", "0");
		testH5(url, params, userName, true);
	}
	/**
	 * 获取回收详情
	 */
	@Test
	public void borrowRecycleDetail() {
		String url = urlBase + "/h5/recycle/borrowRecycleDetail";
		Map<String,String> params = new HashMap<>();
		params.put("borrowId", "3340038");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 获取金融主页
	 */
	@Test
	public void borrowFinanceApi() {
		String url = urlBase + "/h5/recycle/borrowFinanceHome";
		Map<String,String> params = new HashMap<>();
		params.put("start", "0");
		testH5(url, params, userName, true);
	}
	/**
	 * 获取说明
	 */
	@Test
	public void borrowRecycleExplain() {
		String url = urlBase + "/h5/recycle/borrowRecycleExplain";
		testH5(url, null, userName, true);
	}
	/**
	 * 发起回收申请
	 */
	@Test
	public void applyBorrowRecycleCash() {
		String url = urlBase + "/h5/recycle/applyBorrowRecycleCash";
		Map<String,String> params = new HashMap<>();
		params.put("amount", 1000+"");
		params.put("type", 10+"");
		params.put("pwd", DigestUtils.md5Hex("123456")); // 支付密码，根据测试账号需要替换！
		params.put("latitude", "20.35654");
		params.put("longitude", "21.65645");
		params.put("province", "浙江省");
		params.put("city", "杭州市");
		params.put("county", "中国");
		params.put("address", "滨江区星耀城1期");
		params.put("blackBox", "sadasd");
		params.put("bqsBlackBox", "asdasdasd");
		params.put("couponId", "");
		params.put("goodsId", "1");
		params.put("goodsName", "虚拟币");
		params.put("goodsAmount", "10");
		params.put("borrowRemark", "放点");
		params.put("refundRemark", "ce");
		params.put("deliveryAddress", "");
		params.put("deliveryUser", "");
		params.put("deliveryPhone", "13638668564");
		params.put("propertyValue", "456456");
		
		testH5(url, params, userName, true);
	}

	
	/* 模拟三方系统回调 */
	/**
	 * 借钱申请成功后，模拟 UPS 回调 返呗API
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
	 * 回收 取消订单
	 */
	@Test
	public void repayDo() {
		String url = urlBase + "/h5/recycle/recycleRepayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", 1+"");//351.27
		params.put("payPwd", DigestUtils.md5Hex("369369"));
		params.put("cardId", "3111464791");
		params.put("borrowId", "3339771");		
		params.put("bankChannel", "KUAIJIE");		
		
		testH5(url, params, userName, true);
	}
	
	// 重发验证码
	@Test
	public void  resendCode() {
		String url = urlBase + "/h5/pay/recycleQuickPaymentResendCode";
		Map<String,String> params = new HashMap<>();
		params.put("tradeNo", "hq2018052415574200918");

		testH5(url, params, userName ,true);
	}

	
	// 还款验证码确认
	@Test
	public void  repayConfirmPayment() {
		String url = urlBase + "/h5/pay/recycleConfirmPayment";
		Map<String,String> params = new HashMap<>();
		params.put("smsCode", "410468");
		params.put("tradeNo", "hqkj2018042815533300083");

		testH5(url, params, userName ,true);
	}
	
	/**
	 * 支付成功后，模拟 UPS 回调 返呗API
	 */
	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "hq2018050115005300463";
		String merPriv = PayOrderSource.BORROW_RECYCLE_REPAY.getCode();
		String tradeNo = "xianFenghq2018050115005300463";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName, true);
	}
	/* 模拟三方系统回调 */
	
	// 获取银行卡
	@Test
	public void  getUserBankList() {
		String url = urlBase + "/h5/recycle/getUserBankList";
		testH5(url, null, userName, true);
	}
	
	
	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		String url = urlBase + "/third/collection/offlineRepayment?";
		 
		String tradeNo = "offline" + System.currentTimeMillis();
		Map<String,String> params = new HashMap<>();
		params.put("repay_no", tradeNo);
		params.put("borrow_no", "jq2018050811071800489");
		params.put("repay_type", "bank");
		params.put("repay_time", DateUtil.formatDateTime(new Date()));
		params.put("repay_amount", "100.00");
		params.put("rest_amount", "100.00");
		params.put("trade_no", tradeNo);
		params.put("is_balance", YesNoStatus.NO.getCode());
//		params.put("repay_cardNum", "6568654646462113"); // 模拟催收则 注解掉
//		params.put("operator", "测试");// 模拟催收则 注解掉
//		params.put("is_admin", "Y");// 模拟催收则 注解掉
		
		String data = JsonUtil.toJSONString(params);
		String timestamp = DateUtil.getDateTimeFull(new Date());
		String sign = DigestUtil.MD5(data);
		String reqStr = "data=" + URLEncoder.encode(data, "UTF-8") + "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +"&sign="+URLEncoder.encode(sign, "UTF-8");
		url += reqStr;
//		Map<String,String> paramsT = new HashMap<>();
		
		testApi(url, params, userName ,false);
	}
	
}
