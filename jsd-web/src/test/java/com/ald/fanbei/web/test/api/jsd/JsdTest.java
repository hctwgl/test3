package com.ald.fanbei.web.test.api.jsd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.common.util.HttpUtilForXgxy;
import com.ald.fanbei.api.common.util.JsdAesUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.MD5Util;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsdTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//	String urlBase = "https://testapi.51fanbei.com";
	String urlBase = "http://172.20.50.64:8071";
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
     * 同步用户信息
     */
    @Test
    public void syncUserInfo() {
    	String url = urlBase + "/third/eca/v1/syncUserInfo";
    	JSONObject params = new JSONObject();
        params.put("userId", "36C91DFB07EB236DF28CC32187979788");
        params.put("realName", "朱江丰");
        params.put("idNumber", "320324198911057031");
        params.put("bankMobile", "15968196088");
        String data = JsdAesUtil.encryptToBase64Third(JSON.toJSONString(params), AES_KEY);

        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", JsdSignUtil.generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 获取续借详情
     */
    @Test
    public void getRenewalDetailInfo() {
    	String url = urlBase + "/third/eca/v1/getDelayDetail";
        Map<String, String> params = new HashMap<>();
        params.put("borrowNo", "loan0919eca283100000004");
        params.put("timestamp", System.currentTimeMillis()+"");
        params.put("openId", "BC539267586FB64E8990BB3113FCA5BC");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 续期
     */
    @Test
    public void doRenewal() {
    	String url = urlBase + "/third/eca/v1/pushDelay";
    	Map<String, String> goodsInfo = new HashMap<>();
    	goodsInfo.put("goodsName", "大礼包");
    	goodsInfo.put("goodsPrice", "100");
    	goodsInfo.put("goodsImage", "http");

    	Map<String, String> params = new HashMap<>();
    	params.put("borrowNo", "loan0919eca283100000004");
    	params.put("delayNo", "XJ20180919001");
    	params.put("amount", "1000");
    	params.put("delayDay", "7");
    	params.put("bankNo", "6212261202028480466");
    	params.put("isTying", "Y");
    	params.put("tyingType", "BEHEAD");
    	params.put("goodsInfo", goodsInfo.toString());
    	params.put("timestamp", System.currentTimeMillis()+"");
    	params.put("openId", "BC539267586FB64E8990BB3113FCA5BC");
    	String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
    	Map<String, String> p = new HashMap<>();
    	p.put("data", data);
    	p.put("sign", generateSign(params, AES_KEY));
    	String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
    	System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);

//    	testH5(url, params, userName, true);

    }

    /**
     * 还款详情
     */
    @Test
    public void repayLoanDetail() {
        String url = urlBase + "/third/eca/v1/getRepaymentDetail";
        Map<String,String> params = new HashMap<>();
        params.put("borrowNo", "dk2018090221204000156");
        params.put("period", 1+"");
        params.put("timestamp", System.currentTimeMillis()+"");
        params.put("openId","EB56E1F0A9383508DB8FD039C7D37BD1");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }
    /**
     * 还款详情
     */
    @Test
    public void repayLoanBill() {
        String url = urlBase + "/third/eca/v1/getBorrowBill";
        Map<String,String> params = new HashMap<>();
        params.put("borrowNo", "loan0911eca645000000033");
        params.put("openId","EB56E1F0A9383508DB8FD039C7D37BD1");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void bindBankSms() {
        String url = urlBase + "/third/eca/v1/sendMessage";
        Map<String, String> params = new HashMap<>();
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BD1");
        params.put("type","BIND");
        params.put("busiFlag","1313619220301");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    @Test
    public void bankCardBind() {
        String url = urlBase + "/third/eca/v1/bankCardBind";
        Map<String, String> params = new HashMap<>();
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BD1");
        params.put("bankNo","62284803292225524762");
        params.put("bankName","ABC");
        params.put("bankMobile","13136192203");
        params.put("bindNo","1313619220302");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    @Test
    public void bindBankConfirSms() {
        String url = urlBase + "/third/eca/v1/submitMessage";
        Map<String, String> params = new HashMap<>();
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BD1");
        params.put("code","888888");
        params.put("type","BIND");
        params.put("busiFlag","1313619220301");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 还款
     */
    @Test
    public void repayLoan() {
        String url = urlBase + "/third/eca/v1/pushRepayment";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 1+"");
        params.put("period", 1+"");
        params.put("bankNo", "6216696200001010635");
        params.put("borrowNo", "loan1207eca267900000006");
        params.put("repayNo", "dk2018120311375500125");
        params.put("openId","5769BCAB017D030484348D6CB803F080");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    @Test
    public void repayConfiSms() {
        String url = urlBase + "/third/eca/v1/submitMessage";
        Map<String, String> params = new HashMap<>();
        params.put("openId","BC539267586FB64E8990BB3113FCA5BC");
        params.put("code","291855");
        params.put("type","REPAY");
        params.put("busiFlag","repay0911eca267900000192");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void repaySms() {
        String url = urlBase + "/third/eca/v1/sendMessage";
        Map<String, String> params = new HashMap<>();
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BD1");
        params.put("type","REPAY");
        params.put("busiFlag","hqkj20180830151933123019");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),AES_KEY);
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, AES_KEY));
        String respResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p));
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
        return params == null ? null : MD5Util.md5(result.toString());
    }
}
