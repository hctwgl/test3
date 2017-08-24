package com.ald.fanbei.web.test.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseControllerTest;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月23日下午2:59:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BrandShopControllerTest extends BaseControllerTest {

	
	public static final String GET_BRAND_URL      = HTTPHOST + "/brand/getBrandUrl";
	

	@Test
	public void testGetBrandUrl() {
		 try {
	            Map<String, String> params = new HashMap<String, String>();
	            params.put("shopId", "1");
	            this.testApi(GET_BRAND_URL, params);   
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	
}
