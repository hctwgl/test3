package com.ald.fanbei.web.test.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.web.test.common.BaseControllerTest;

/**
 *@类描述：
 *@author xiaotianjian 2017年2月7日下午2:57:48
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UserServiceTest extends BaseControllerTest {
	
	@Resource
	AfUserService afUserService;
	
	@Test
	public void testUserService() {
		System.out.println(afUserService.getUserById(8L));
	}
	
	
}
