package com.ald.fanbei.web.test.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.web.test.common.BaseTest;
import com.ald.fanbei.web.test.common.RedisClient;

public class AccountAppealTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://localhost:8080";
	String oldMoble = "13370127054";
	String newMobile = "15869648535";
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(oldMoble);
	}
	
//	@Test
	public void  testGetVerifyCodeApi() {
		String url = urlBase + "/user/getVerifyCode";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", newMobile);
		params.put("type", "M");
		testApi(url, params, oldMoble ,false);
	}
	
//	@Test
	public void accountAppealCheckSms(){
		String url = urlBase + "/user/accountAppealCheckSms";
		Map<String,String> params = new HashMap<>();
		params.put("verifyCode", "888888");
		params.put("oldMobile", oldMoble);
		params.put("newMobile", newMobile);
		testApi(url, params, oldMoble, false);
	}
	
	@Test
	public void getFaceTypeFree(){
		String url = urlBase + "/auth/getFaceTypeFree";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, "", false);
	}
	
//	@Test
	public void updateRealnameManual(){
		String url = urlBase + "/auth/updateRealnameManualFree";
		Map<String,String> params = new HashMap<>();
		params.put("oldMobile", oldMoble);
		params.put("realname", "王卿");
		testApi(url, params, "", false);
		
		RedisClient.setRaw(Constants.CACHEKEY_REAL_AUTH_CITIZEN_CARD_PREFFIX + oldMoble, "130125198801118542");
		RedisClient.setRaw(Constants.CACHEKEY_REAL_AUTH_PASS_PREFFIX + oldMoble, 1);
	}
	
//	@Test
	public void accountAppealDo(){
		String url = urlBase + "/user/accountAppealDo";
		Map<String,String> params = new HashMap<>();
		params.put("oldMobile", oldMoble);
		params.put("password", DigestUtils.md5Hex("88888888"));
		testApi(url, params, oldMoble, false);
	}
	
//	@Test
	public void yituUploadIdCard() {
		String url = urlBase + "/file/yituUploadIdCardFree.htm";
		Map<String,String> params = new HashMap<>();
		testApi(url, params, oldMoble, false);
	}
	
//	@Test
	public void flow() {
		try {
			testGetVerifyCodeApi();
			TimeUnit.SECONDS.sleep(2);
			
			accountAppealCheckSms();
			TimeUnit.SECONDS.sleep(2);
			
			updateRealnameManual();
			TimeUnit.SECONDS.sleep(2);
			
			accountAppealDo();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
