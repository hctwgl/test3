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
//	String urlBase = "https://testapp.51fanbei.com";
	String urlBase = "http://localhost:8080";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
//	String userName = "15669066271";	//田建成 cardId:3111464125 支付密码123456
	String userName = "13958004662";	//胡潮永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
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
	public void authUserBasic(){
		String url = urlBase + "/auth/checkUserBasicInfo";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, userName, true);
	}
    @Test
    public void  checkUserIdCardInfo(){
        String url = urlBase + "/auth/checkUserIdCardInfo";
        Map<String,String> params = new HashMap<>();
        params.put("idNumber", "130125198801118542a");
        testApi(url, params, userName, true);
    }
	/*----------------
	 *  绑卡业务 start
	 *---------------- */
	/**
	 * 绑卡-提交
	 */
	@Test
	public void submitBindBankcard() {
		String url = urlBase + "/auth/submitBindBankcard";
		Map<String,String> params = new HashMap<>();
		params.put("bankCardId", "3111202");
		params.put("payPwd", DigestUtils.md5Hex("123456")); // 支付密码，根据测试账号需要替换！
		params.put("realname", "胡潮永");
		params.put("idNumber", "330624198509136450");
		params.put("smsCode", "65888");
		
		testApi(url, params, userName, true);
	}
	
	/**
	 * 绑卡-申请短信
	 */
	@Test
	public void applyBindBankcard() {
		String url = urlBase + "/auth/applyBindBankcard";
		Map<String,String> params = new HashMap<>();
		params.put("cardNumber", "6222620170007941112");
		params.put("mobile", "13958004662");
		params.put("bankCode", "BCOM");
		params.put("bankName", "招商银行");
		params.put("realname", "胡潮永");
		
		params.put("idNumber", "330624198509136450");
		
		testApi(url, params, userName, true);
	}
	
	/*----------------
	 *  绑卡业务 end
	 *---------------- */

}
