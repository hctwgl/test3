package com.ald.fanbei.web.test.api.user;

import com.ald.fanbei.web.test.common.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Login extends BaseTest {

    String urlBase = "http://localhost:8080";
    String userName = "13136192203";
    /**
     * 自动注入登陆令牌
     */
    @Before
    public void init() {
        super.init(userName);
    }

    @Test
    public void testLogin(){
        String url = urlBase + "/user/login";
        Map params = new HashMap<>();
        params.put("osType","5.1.1");
        params.put("phoneType","vivo X7");
        params.put("uuid","33sssss");
        params.put("wifi_mac","test");
        params.put("password","123456");
        params.put("loginType","1");
        testApi(url, params, userName, false);

    }
}
