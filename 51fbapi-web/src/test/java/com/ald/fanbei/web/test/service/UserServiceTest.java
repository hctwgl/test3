package com.ald.fanbei.web.test.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.web.test.common.BaseControllerTest;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

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
