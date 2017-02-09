package com.ald.fanbei.web.test.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseControllerTest;

/**
 *@类描述：
 *@author xiaotianjian 2017年2月9日上午11:01:33
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GoodsControllerTest extends BaseControllerTest {

	
	public static final String SEARCH_GOODS      = HTTPHOST + "/goods/searchGoods";
	public static final String HOME      = HTTPHOST + "/goods/getHomeInfo";
	
	
	@Test
	public void testSearchGoods() {
		 try {
	            Map<String, String> params = new HashMap<String, String>();
	           //登陆测试 
	            params.put("q", "男装");
	            this.testApi(SEARCH_GOODS, params);   
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	@Test
	public void testaas() {
		 try {
	            Map<String, String> params = new HashMap<String, String>();
	           //登陆测试 
	            params.put("q", "男装");
	            this.testApi(SEARCH_GOODS, params);   
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	
}
