package com.ald.fanbei.web.test.api.dsed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class DsedbindCardTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//	String urlBase = "https://testapi.51fanbei.com";
//    String urlBase = "http://192.168.112.40:8080";
    String urlBase = "http://localhost:8078";

    String userName = AccountOfTester.夏枫.mobile;

    /**
     * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
     */
    @Before
    public void init() {
        super.init(userName);
    }

    /**
     * 发起贷款申请
     */
    @Test
    public void getAddressList() {
        String url = urlBase + "/third/xgxy/v1/getAddressList";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "10,12");

        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }
    @Test
    public void getSms() {
        String url = urlBase+ "/third/xgxy/v1/getSmsCode";
        Map<String, String> params = new HashMap<>();
        params.put("busiFlag", "3111465291");
        params.put("type", SmsCodeType.BIND.getCode());
        params.put("userId","1C9064925F3AAF85BC663FEB1727DD4B");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void dsedSubmitBind() {
        String url = urlBase + "/third/xgxy/v1/smsCodeSubmit";
        Map<String, String> params = new HashMap<>();
        params.put("busiFlag", "3111465291");
        params.put("code", "122761");
        params.put("type", SmsCodeType.BIND.getCode());
        params.put("userId","1C9064925F3AAF85BC663FEB1727DD4B");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }
    @Test
    public void getContacts() {
        String url = urlBase + "/third/xgxy/v1/bankCardBind";
        Map<String, String> params = new HashMap<>();
        params.put("userId","BFD284DE654934A56F1BFD4048CE9963");
        params.put("bankNo","6228480329222552476");
        params.put("bankName","农业银行");
        params.put("bankMobile","13136192203");
        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void testpreFinish() {
        String url = urlBase + "/third/xgxy/v1/preFinish";
        Map<String, String> params = new HashMap<>();
        params.put("userId","E272FA8BFB4071C42DB0C2921339F8C2");
        params.put("amount","5.0");
        params.put("borrowNo","dk2018071100274300110");
        params.put("bankNo","6214835897082325");

        String data = JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
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

    public static String paramsEncrypt(Map<String, String> params, String appSecret) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        JSONObject obj = new JSONObject(true);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            obj.put(key, value);
        }
        String result = obj.toString();
        result = AesUtil.encryptToBase64(result, appSecret);
        return result;
    }

}
