package com.ald.fanbei.web.test.api.jsd;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.util.DigestUtil;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DsedSignUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsdTest extends BaseTest {
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
    public void getRenewalDetailInfo() {
    	String url = urlBase + "/third/eca/v1/getDelayDetail";
        Map<String, String> params = new HashMap<>();
        params.put("borrowNo", "BO20180829001");
        params.put("timestamp", System.currentTimeMillis()+"");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 获取续借详情
     */
    @Test
    public void doRenewal() {
    	String url = urlBase + "/third/eca/v1/pushDelay";
    	Map<String, String> goodsInfo = new HashMap<>();
    	goodsInfo.put("goodsName", "大礼包");
    	goodsInfo.put("goodsPrice", "100");
    	goodsInfo.put("goodsImage", "http");

    	Map<String, String> params = new HashMap<>();
    	params.put("borrowNo", "BO20180829001");
    	params.put("delayNo", "XJ20180829001");
    	params.put("amount", "1000");
    	params.put("delayDay", "10");
    	params.put("bankNo", "6212261202028480466");
    	params.put("isTying", "Y");
    	params.put("tyingType", "SELL");
    	params.put("goodsInfo", goodsInfo.toString());
    	params.put("timestamp", System.currentTimeMillis()+"");
    	params.put("userId", "EB56E1F0A9383508DB8FD039C7D37BDF");
    	String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
    	Map<String, String> p = new HashMap<>();
    	p.put("data", data);
    	p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
    	String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
    	System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    	
//    	testH5(url, params, userName, true);
    	
    }


    /**
     * 还款
     */
    @Test
    public void repayLoan() {
        String url = urlBase + "/third/xgxy/v1/pushRepayment";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 0.01+"");
        params.put("period", 1+"");
        params.put("bankNo", "6212261202028480466");
        params.put("borrowNo", "BO20180829002");
        params.put("repayNo", "rn20180829002");
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BD1");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
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
