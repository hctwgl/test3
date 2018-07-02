package com.ald.fanbei.web.test.api.user;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class RedRainTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://192.168.106.162:8180";
	String userName = "13656640521";
	
	/**
	 * 自动注入登陆令牌
	 */
//	@Before
	public void init(){
		super.init(userName);
	}
	
//	@Test
	public void  testGetRedRainRoundsApi() {
		String url = urlBase + "/pushClickAmout/clickPushAmountNum";
		Map<String,String> params = new HashMap<>();
		params.put("pushId", "82");
		testApi(url, params, userName, true);
	}
	
//	@Test
	public void  testApplyHitH5() {
		String url = urlBase + "/fanbei-web/redRain/applyHit";
		Map<String,String> params = new HashMap<>();
 		testH5(url, params, userName, false);
	}
	
//	@Test
	public void  fetchRounds() {
		String url = urlBase + "/fanbei-web/redRain/fetchRounds";
		Map<String,String> params = new HashMap<>();
 		testH5(url, params, userName, false);
	}
	
	@Test
	public void performanceTest() {
		ExecutorService executer = Executors.newFixedThreadPool(500);
		
		try {
			String s = FileUtils.readFileToString(new File(this.getClass().getClassLoader().getResource("userNames.json").toURI()));
			final JSONArray arr = JSON.parseArray(s);
			// 5万请求时会挂掉，降低测试基准改为1万 int size = arr.size(); 
			final String url = urlBase + "/fanbei-web/redRain/applyHit";
			
			for(int i = 0; i<10000; i++) {
				final String userName = arr.getJSONObject(i).getString("user_name");
				final TokenBo tokenBo = init(userName);
				
				executer.execute(new Runnable() {
					public void run() {
						Map<String,String> params = new HashMap<>();
				 		testH5(url, params, userName, false, tokenBo);
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
}
