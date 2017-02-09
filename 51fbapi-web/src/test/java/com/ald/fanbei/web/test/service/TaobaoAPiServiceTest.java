package com.ald.fanbei.web.test.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.web.test.common.BaseControllerTest;
import com.taobao.api.ApiException;

/**
 *@类描述：
 *@author xiaotianjian 2017年2月8日下午7:33:51
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoAPiServiceTest extends BaseControllerTest {
	
	@Resource
	TaobaoApiUtil taobaoApiUtil;
	
	@Test
	public void testTaobaoApi() throws ApiException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "女装");
		params.put("sort", "total_sales_desc");
		params.put("startPrice", 100);
		params.put("endPrice", 200);
		params.put("isTmall", false);
		params.put("pageNo", 1L);
		
		System.out.println(taobaoApiUtil.executeTaobaokeSearch(params));
		
	}
}

