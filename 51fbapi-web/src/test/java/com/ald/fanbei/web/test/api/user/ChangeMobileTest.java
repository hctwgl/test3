package com.ald.fanbei.web.test.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

/**
 * 更换手机功能 测试类
 * @author zhujiangfeng
 *
 */
public class ChangeMobileTest extends BaseTest{
	
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://localhost:8080";
	String userName = "15968196088";
	
	
	/**
	 * 自动注入登陆令牌
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
//	@Test
	public void  testChangeMobileIfAbleApi() {
		String url = urlBase + "/user/changeMobileIfAble";
		testApi(url, null, userName);
	}
	
//	@Test
	public void  testGetVerifyCodeApi() {
		String url = urlBase + "/user/getVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", "15968196088");
		params.put("type", "M");
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  testCheckVerifyCodeApi() {
		String url = urlBase + "/user/checkVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("type", "M");
		
		testApi(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileCheckVerifyCodeApi() {
		String url = urlBase + "/user/changeMobileCheckVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("newMobile", "15968196088");
		params.put("type", "M");
		
		testApi(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileVerifyApi() {
		String url = urlBase + "/user/changeMobileVerify";
		Map<String,String> params = new HashMap<>();
//		params.put("behavior", "PAY_PWD");
//		params.put("payPwd", DigestUtils.md5Hex("123456"));
		
		params.put("behavior", "OLD_MOBILE");
		params.put("verifyCode", "888888");
		
//		params.put("behavior", "ID_CARD");
//		params.put("idCard", "3301271990020147330");
		
		params.put("newMobile", "15968196088");
		
		testApi(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileSyncConactsApi() {
		String url = urlBase + "/user/changeMobileSyncConacts";
		Map<String,String> params = new HashMap<>();
		params.put("contacts", "陈金虎:15958686524&18857416845,小猪:07966898475,她娘:18656847587");
		
		testApi(url, params, userName);
	}
	
	@Test
	public void doFlow() throws InterruptedException {
		testGetVerifyCodeApi();
		TimeUnit.SECONDS.sleep(2);
		
		testChangeMobileCheckVerifyCodeApi();
		
//		testChangeMobileVerifyApi();
//		TimeUnit.SECONDS.sleep(2);
//		
//		testChangeMobileSyncConactsApi();
	}
	
}
