package com.ald.fanbei.web.test.api.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class AccountAppealTest extends BaseTest{
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
	public void updateRealnameManual(){
		String url = urlBase + "/auth/updateRealnameManual";
		Map<String,String> params = new HashMap<>();
		params.put("realname", "江枫");
		testApi(url, params, userName, false);
	}
	
	@Test
	public void accountAppealCheckSms(){
		String url = urlBase + "/user/accountAppealCheckSms";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", "888888");
		params.put("newMobile", "15869648535");
		testApi(url, params, userName, false);
	}
	
	@Test
	public void accountAppealDo(){
		String url = urlBase + "/user/accountAppealDo";
		Map<String,String> params = new HashMap<>();
		params.put("password", DigestUtils.md5Hex("8888888888"));
		testApi(url, params, userName, false);
	}
	
}
