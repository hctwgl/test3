package com.ald.fanbei.web.test.api.user;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class RedRainTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://192.168.106.162:8180";
	String userName = "13370127054";
	
	/**
	 * 自动注入登陆令牌
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	@Test
	public void  testGetRedRainRoundsApi() {
		String url = urlBase + "/resource/getRedRainRounds";
		testApi(url, null, userName, true);
	}
	
//	@Test
	public void  testApplyHitH5() {
		String url = urlBase + "/fanbei-web/redRain/applyHit";
		Map<String,String> params = new HashMap<>();
 		testH5(url, params, userName);
	}
}
