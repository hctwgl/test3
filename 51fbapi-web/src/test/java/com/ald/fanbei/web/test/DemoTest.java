package com.ald.fanbei.web.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class DemoTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://192.168.106.162:8180";
	String userName = "13656640521";
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	@Test
	public void demoApiNoLogin(){
		String url = urlBase + "/resource/getRedRainRounds";
		Map<String,String> params = new HashMap<>();
		params.put("pushId", "82");
		params.put("www", "qqq");
		testApi(url, params, userName, false);
	}
	
	@Test
	public void  demoApi() {
		String url = urlBase + "/pushClickAmout/clickPushAmountNum";
		Map<String,String> params = new HashMap<>();
		params.put("demoParams", "2");
		testApi(url, params, userName ,true);
	}
	
	@Test
	public void  demoH5() {
		String url = urlBase + "/fanbei-web/redRain/applyHit";
		Map<String,String> params = new HashMap<>();
		params.put("demoParams", "1");
 		testH5(url, params, userName, true);
	}
	
	/**
	 * 测试完成业务流
	 * 示例业务(更换手机号)：1发起短信 -> 2验证短信 -> 3更换手机号
	 */
//	@Test
	public void demoFlow() throws InterruptedException {
//		testGetVerifyCodeApi(); 1
		TimeUnit.SECONDS.sleep(2);
		
//		testChangeMobileVerifyApi(); 2
		TimeUnit.SECONDS.sleep(2);
		
//		testChangeMobileSyncConactsApi(); 3
	}
	
}
