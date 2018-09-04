package com.ald.fanbei.web.test.api.jsd;


import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
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
    
    private static final String AES_KEY = "9c5dd35d58f8501f";

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
    	JSONObject params = new JSONObject();
        params.put("openId", "36C91DFB07EB236DF28CC321871E6A7D");
        params.put("productNo", "2");
        params.put("amount", "5000");
        params.put("term", "10");
        params.put("unit", "DAY");
        params.put("isTying", "Y");
        params.put("tyingType", "SELL");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), AES_KEY);
        Map<String, Object> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", JsdSignUtil.generateSign(params, AES_KEY));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }
    
    /**
     * 发起借钱申请
     */
    @Test
    public void pushBorrow() {
        String url = urlBase + "/third/eca/v1/pushBorrow";
        JSONObject params = new JSONObject();
        params.put("openId", "36C91DFB07EB236DF28CC321871E6A7D");
        params.put("productNo", "2");
        params.put("borrowNo", "xgxy202314123090123123");
        params.put("amount", "5000");
        params.put("term", "10");
        params.put("unit", "DAY");
        params.put("loanRemark", "买车");
        params.put("repayRemark", "工资");
        params.put("bankNo", "6212261202028480466");
        params.put("isTying", "Y");
        params.put("tyingType", "SELL");
        
        JSONObject goodsInfo = new JSONObject();
        goodsInfo.put("goodsName", "胸罩");
        goodsInfo.put("goodsPrice", "500");
        goodsInfo.put("goodsImage", "http://img");
        params.put("goodsInfo", goodsInfo);
        
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", JsdSignUtil.generateSign(params, AES_KEY));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }
}
