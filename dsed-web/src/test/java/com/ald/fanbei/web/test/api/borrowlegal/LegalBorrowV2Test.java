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
//	String urlBase = "https://atestapp.51fanbei.com";
	String urlBase = "http://localhost:8080";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
	String userName = "18258023758";	//田建成 cardId:3111464125 支付密码123456
//	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888 登陆q123456
//	String userName = "13370127054";	//王卿 	支付密码123456
//	String userName = "13656648524";	//朱玲玲 支付密码123456
//	String userName = "13510301615";	//王绪武 支付密码123456
//	String userName = "17612158083";	//代秋田
//	String userName = "17756648524";	//新账号 支付密码123456
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	
	@Test
	public void borrow() {
		String url = urlBase + "/legalborrowV2/applyLegalBorrowCash"; // TODO
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
		params.put("borrowRemark", "");
		params.put("refundRemark", "");
		params.put("deliveryAddress", "");
		params.put("deliveryUser", "");
		params.put("deliveryPhone", "13638668564");
		params.put("gameId", "59");
		
		testApi(url, params, userName ,true);
	}
	
	@Test
	public void  delegatePay() {
		String url = urlBase + "/third/ups/delegatePay?";
		String orderNo = "jq2018041614095300106";
		String merPriv = PayOrderSource.BORROWCASH.getCode();
		String tradeState = "00";
		String reqExt = "3339804";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&reqExt=" + reqExt + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		
		testApi(url, params, userName ,true);
	}
	
	@Test
	public void renewal() {
		String url = urlBase + "/legalborrowV2/confirmLegalRenewalPay";
		Map<String,String> params = new HashMap<>();
		
		params.put("borrowId", "3339801");
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("renewalAmount", "200");
		params.put("cardId", "3111464125");
		params.put("goodsId", "1");
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
	
	@Test
	public void  collect() {
		String url = urlBase + "/third/ups/collect?";
		String orderNo = "hq2018071618000400030";
		String merPriv = PayOrderSource.REPAY_LOAN.getCode();
		String tradeNo = "xianfeng21231";
		String tradeState = "00";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
		url += reqStr;
		Map<String,String> params = new HashMap<>();
		

		testApi(url, params, userName ,false);
	}
	
	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		String url = urlBase + "/third/collection/offlineRepayment?";
		 
		String tradeNo = "offline" + System.currentTimeMillis();
		Map<String,String> params = new HashMap<>();
		params.put("repay_no", tradeNo);
		params.put("borrow_no", "jq2017122020002200873");
		params.put("repay_type", "bank");
		params.put("repay_time", DateUtil.formatDateTime(new Date()));
		params.put("repay_amount", "10000.00");
		params.put("rest_amount", "10000.00");
//		params.put("repay_cardNum", "6568654646462113"); // 模拟催收则 注解掉
//		params.put("operator", "测试");// 模拟催收则 注解掉
		params.put("trade_no", tradeNo);
		params.put("is_balance", YesNoStatus.NO.getCode());
//		params.put("is_admin", "Y");// 模拟催收则 注解掉
		
		String data = JsonUtil.toJSONString(params);
		String timestamp = DateUtil.getDateTimeFull(new Date());
		String sign = DigestUtil.MD5(data);
		String reqStr = "data=" + URLEncoder.encode(data, "UTF-8") + "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +"&sign="+URLEncoder.encode(sign, "UTF-8");
		url += reqStr;
		Map<String,String> paramsT = new HashMap<>();
		
		testApi(url, paramsT, userName ,false);
	}
	
	
	@Test
	public void  homePage() {
		String url = urlBase + "/legalborrowV2/getLegalBorrowCashHomeInfo";
		Map<String,String> params = new HashMap<>();		
		testApi(url, params, userName ,true);
	}
}
