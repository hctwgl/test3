package com.ald.fanbei.web.test.api.jsd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsdBorrowTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//	String urlBase = "https://testapi.51fanbei.com";
	String urlBase = "http://localhost:8078";
//    String urlBase = "http://192.168.112.40:8080";
    
    String userName = "13165995223";

    /**
     * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
     */
    @Before
    public void init() {
        super.init(userName);
    }

    /**
     * 获取续借详情
     */
    @Test
    public void getBorrowFeeDetail() {
    	String url = urlBase + "/third/eca/v1/getBorrowFeeDetail";
        Map<String, String> params = new HashMap<>();
        params.put("openId", "36C91DFB07EB236DF28CC321871E6A7D");
        params.put("productNo", "2");
        params.put("amount", "5000");
        params.put("term", "10");
        params.put("unit", "DAY");
        params.put("isTying", "Y");
        params.put("tyingType", "SELL");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"9c5dd35d58f8501f");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "9c5dd35d58f8501f"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 生成本地签名
     *
     * @param params
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static String generateSign(Map<String, String> params, String appSecret) throws IllegalArgumentException {
        List<String> keys = new ArrayList<String>(params.keySet());
        keys.remove("signCode");
        Collections.sort(keys);
        StringBuffer result = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i == 0) result = new StringBuffer();
            else result.append("&");
            result.append(key).append("=").append(params.get(key));
        }
        result.append("&appSecret=" + appSecret);
        return params == null ? null : MD5.md5(result.toString());
    }

}
