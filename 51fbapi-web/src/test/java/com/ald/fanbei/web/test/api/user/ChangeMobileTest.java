package com.ald.fanbei.web.test.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.web.test.common.BaseTest;

/**
 * 更换手机功能 测试类
 * @author zhujiangfeng
 *
 */
public class ChangeMobileTest extends BaseTest{
	
//	@Test
	public void  testChangeMobileIfAbleApi() {
		String url = "http://localhost:8080/user/changeMobileIfAble";
		String userName = "15968196088";
		testApiSkipSign(url, null, userName);
	}
	
//	@Test
	public void  testGetVerifyCodeApi() {
		String url = "http://localhost:8080/user/getVerifyCode";
		String userName = "15968196088";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", "15968196088");
		params.put("type", "M");
		testApiSkipSign(url, params, userName);
	}
	
//	@Test
	public void  testCheckVerifyCodeApi() {
		String url = "http://localhost:8080/user/checkVerifyCode";
		String userName = "15968196088";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("type", "M");
		
		testApiSkipSign(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileCheckVerifyCodeApi() {
		String url = "http://localhost:8080/user/changeMobileCheckVerifyCode";
		String userName = "15968196088";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("type", "M");
		
		testApiSkipSign(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileVerifyApi() {
		String url = "http://localhost:8080/user/changeMobileVerify";
		String userName = "15968196088";
		Map<String,String> params = new HashMap<>();
//		params.put("behavior", "PAY_PWD");
//		params.put("payPwd", DigestUtils.md5Hex("123456"));
		
		params.put("behavior", "OLD_MOBILE");
		params.put("verifyCode", "888888");
		
//		params.put("behavior", "ID_CARD");
//		params.put("idCard", "3301271990020147330");
		
		params.put("newMobile", "15968196088");
		
		testApiSkipSign(url, params, userName);
	}
	
//	@Test
	public void  testChangeMobileSyncConactsApi() {
		String url = "http://localhost:8080/user/changeMobileSyncConacts";
		String userName = "15968196088";
		Map<String,String> params = new HashMap<>();
		params.put("contacts", "陈金虎:15958686524&18857416845,小猪:07966898475,她娘:18656847587");
		
		testApiSkipSign(url, params, userName);
	}
	
//	@Test
	public void doFlow() throws InterruptedException {
		testGetVerifyCodeApi();
		TimeUnit.SECONDS.sleep(2);
		
		testChangeMobileVerifyApi();
		TimeUnit.SECONDS.sleep(2);
		
		testChangeMobileSyncConactsApi();
	}
	
	@Test
	public void test() {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.info("Hello World!");
	}
	
}
