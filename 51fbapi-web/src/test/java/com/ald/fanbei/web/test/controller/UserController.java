package com.ald.fanbei.web.test.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseControllerTest;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年2月6日下午3:17:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UserController extends BaseControllerTest {
    
	public static final String USER_LOGIN      = HTTPHOST + "/user/login";
	
    @Test
    public void getLoginTest() {
        try {
            Map<String, String> params = new HashMap<String, String>();
           //登陆测试 
            params.put("password", "e10adc3949ba59abbe56e057f20f883e1");
            params.put("osType", "ios");
            params.put("phoneType", "Meizunote3");
            params.put("uuid", "qwthuer151");
            this.testApi(USER_LOGIN, params);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


