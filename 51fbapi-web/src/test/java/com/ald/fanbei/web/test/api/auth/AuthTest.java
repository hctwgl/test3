package com.ald.fanbei.web.test.api.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class AuthTest  extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "https://atestapp.51fanbei.com";
//	String urlBase = "http://localhost:8080";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
//	String userName = "15669066271";	//田建成 cardId:3111464125 支付密码123456
//	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
	String userName = "13370127054";	//王卿 	支付密码123456
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
	
	/*----------------
	 *  绑卡业务 start
	 *---------------- */
	/**
	 * 发起贷款申请
	 */
	@Test
	public void applyLoan() {
		String url = urlBase + "/auth//submitBindBankcard";
		Map<String,String> params = new HashMap<>();
		params.put("orderId", "65656565");
		params.put("amount", 1000+"");
		params.put("payPwd", DigestUtils.md5Hex("123456")); // 支付密码，根据测试账号需要替换！
		params.put("realname", "王卿");
		
		params.put("idNumber", "3203265648764614");
		params.put("cardNumber", "62398656789");
		params.put("mobile", "13765482698");
		params.put("bankCode", "CBD");
		params.put("bankName", "中国银行");
		params.put("smsCode", "65888");
		
		testApi(url, params, userName, true);
	}
	
	/*----------------
	 *  绑卡业务 end
	 *---------------- */

}
