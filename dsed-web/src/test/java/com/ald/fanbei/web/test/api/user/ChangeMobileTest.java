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
	String urlBase = "http://192.168.106.162:8180";
	String userName = "13656640522";
	
	String oldMobile = "13656640522";
	String newMobile = "13656640521";
	
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
		testApi(url, null, userName, true);
	}
	
//	@Test
	public void  testGetVerifyCodeApi() {
		String url = urlBase + "/user/getVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", oldMobile);
		params.put("type", "M");
		testApi(url, params, userName ,true);
	}
	
//	@Test
	public void  testCheckVerifyCodeApi() {
		String url = urlBase + "/user/checkVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("type", "M");
		
		testApi(url, params, userName, true);
	}
	
	@Test
	public void  testChangeMobileCheckVerifyCodeApi() {
		String url = urlBase + "/user/changeMobileCheckVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", String.valueOf(888888));
		params.put("newMobile", newMobile);
		params.put("type", "M");
		
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void  testChangeMobileVerifyApi() {
		String url = urlBase + "/user/changeMobileVerify";
		Map<String,String> params = new HashMap<>();
//		params.put("behavior", "PAY_PWD");
//		params.put("payPwd", DigestUtils.md5Hex("a123456"));
		
		params.put("behavior", "OLD_MOBILE");
		params.put("verifyCode", "888888");
		
//		params.put("behavior", "ID_CARD");
//		params.put("idCard", "3301271990020147330");
		
		params.put("newMobile", newMobile);
		
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void  testChangeMobileSyncConactsApi() {
		String url = urlBase + "/user/changeMobileSyncConacts";
		Map<String,String> params = new HashMap<>();
		params.put("contacts", "chenjinhu:18656899997,xiaozhu:18656899998,taniang:18656899999");
		
		testApi(url, params, userName, true);
	}
	
	
	public void  testAuthContactsV1() {
		String url = urlBase + "/auth/authContactsV1";
		Map<String,String> params = new HashMap<>();
		params.put("contacts", "陈金虎:18656899997,小猪:18656899998,她娘:18656899999");
		
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void doFlow() throws InterruptedException {
		testGetVerifyCodeApi();
		TimeUnit.SECONDS.sleep(2);
		
		testChangeMobileVerifyApi();
		TimeUnit.SECONDS.sleep(2);
		
		testChangeMobileSyncConactsApi();
		
//		testAuthContactsV1();
	}
	
}
