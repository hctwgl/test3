package com.ald.fanbei.web.test.api.user;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class RedRainTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://localhost:8080";
	String userName = "13656640521";
	
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
}
