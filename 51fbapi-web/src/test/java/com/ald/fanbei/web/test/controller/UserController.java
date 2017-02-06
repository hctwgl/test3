package com.ald.fanbei.web.test.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseControllerTest;


public class UserController extends BaseControllerTest {
    
	public static final String USER_LOGIN      = HTTPHOST + "/user/login";
	
    @Test
    public void getLoginTest() {
        try {
            Map<String, String> params = new HashMap<String, String>();
           //登陆测试 
            params.put("password", "123456");
            params.put("userName", "chenjinhu");
            this.testApi(USER_LOGIN, params);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


