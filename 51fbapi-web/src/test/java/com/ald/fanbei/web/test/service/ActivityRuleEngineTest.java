package com.ald.fanbei.web.test.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.web.test.common.BaseControllerTest;

/**
 *@类描述：
 *@author xiaotianjian 2017年2月7日下午3:12:40
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ActivityRuleEngineTest extends BaseControllerTest {

	@Resource
	CouponSceneRuleEnginerUtil activeRuleEngineUtil;
	
	@Test
	public void registActivityEngine() {
		activeRuleEngineUtil.regist(8L,null);
	}
	
}
