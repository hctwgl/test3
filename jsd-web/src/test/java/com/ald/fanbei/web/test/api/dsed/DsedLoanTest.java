package com.ald.fanbei.web.test.api.dsed;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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

public class DsedLoanTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//	String urlBase = "https://testapi.51fanbei.com";
  String urlBase = "http://localhost:8080";
//    String urlBase = "http://192.168.112.40:8080";
    
    String userName = "18258023758";

    /**
     * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
     */
    @Before
    public void init() {
        super.init(userName);
    }

    /**
     * 获取借钱首页详情
     */
    @Test
    public void getHomeInfo() {
        String msg = String.format("该银行单笔限额%.2f元，请使用其他银行卡还款，谢谢！", 0.1);
        String url = urlBase + "/h5/loan/getLoanHomeInfo";
        testH5(url, null, userName, true);
    }

    /**
     * 贷款同步用户接口
     */
    @Test
    public void dsedSyncUserInfo() {
        String url = urlBase + "/third/xgxy/v1/syncUserInfo";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "18637962344");
        params.put("realName", "郭帅强");
        params.put("idNumber", "330724199211254817");
        params.put("mobile", "13018933980");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 贷款查询接口
     */
    @Test
    public void dsedGetLoanInfo() {
        String url = urlBase + "/third/xgxy/v1/getBorrowInfo";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "4B697C46BC0A3D2F32915F5CBDBE8567");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    /**
     * 发起贷款申请
     */
    @Test
    public void dsedApplyLoan() {
        String url = urlBase + "/third/xgxy/v1/comfirmBorrow";
        Map<String, String> params = new HashMap<>();
        params.put("prdType", "DSED_LOAN");
        params.put("amount", 6000 + "");
        params.put("userId", "1C9064925F3AAF85BC663FEB1727DD4B");
        params.put("period", 3 + "");
        params.put("realName", "过帅强");
        params.put("loanRemark", "装修");
        params.put("repayRemark", "工资");
        params.put("bankNo", "6214835896219365");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    /**
     * 发起贷款申请
     */
    @Test
    public void dsedCollectRepay() throws UnsupportedEncodingException {
        String url = urlBase + "/third/collection/offLineBackMoney";
        Map<String, String> data = new HashMap<>();
        String salt = "dsedcuishou";
        data.put("orderNo","333");
        data.put("totalAmount","12.54");
        data.put("repaymentAcc","13989455831");
        data.put("details","[{'dataId':'1251','amount':'4.27'},{'dataId':'1252','amount':'4.18'},{'dataId':'1253','amount':'4.09'}]");
        data.put("repayTime","2018-07-18 11:02:46");
        data.put("companyId","3");
        data.put("type","10");
        data.put("repaymentNo","333");
        Map<String, String> params = new HashMap<>();
        JSON.toJSONString(data);
        params.put("data",JSON.toJSONString(data));
        String a = JSON.toJSONString(data);
        byte[] pd = DigestUtil.digestString(a.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
        String sign1 = DigestUtil.encodeHex(pd);
        params.put("sign",sign1);
        HttpUtil.post(url,params);

//        params.put("prdType", "DSED_LOAN");
//        params.put("amount", 6000 + "");
//        params.put("userId", "1C9064925F3AAF85BC663FEB1727DD4B");
//        params.put("period", 3 + "");
//        params.put("realName", "过帅强");
//        params.put("loanRemark", "装修");
//        params.put("repayRemark", "工资");
//        params.put("bankNo", "6214835896219365");
//        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
//        Map<String, String> p = new HashMap<>();
//        p.put("data", data);
//        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
//        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));

//        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    @Test
    public void dsedrepayLoan() {
        String url = urlBase + "/third/xgxy/v1/pushRepayment";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 0.01+"");
        params.put("curPeriod", 1+"");
        params.put("bankNo", "6214855713637987");
        params.put("borrowNo", "dk2018071302455300249");
        params.put("userId","EB56E1F0A9383508DB8FD039C7D37BDF");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void dsedrepayPreFinish() {
        String url = urlBase + "/third/xgxy/v1/preFinish";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 9.26+"");
        params.put("curPeriod", 1+"");
        params.put("bankNo", "6214835890543422");
        params.put("borrowNo", "dk2018071002395100033");
        params.put("userId","1C9064925F3AAF85BC663FEB1727DD4B");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void  collect() {
        String url = urlBase + "/third/ups/collect?";
        String orderNo = "hq2018071618000400030";
        String merPriv = PayOrderSource.REPAY_LOAN.getCode();
        String tradeNo = "xianfeng21231";
        String tradeState = "00";

        String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
        url += reqStr;
        Map<String,String> params = new HashMap<>();


        testApi(url, params, userName ,true);
    }


    @Test
    public void getBorrowFeeDetail() {
        String url = urlBase + "/third/xgxy/v1/getBorrowFeeDetail";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 1+"");
        params.put("periods", "2");
        params.put("userId","tjc18637962344");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
        
        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }

    @Test
    public void getProtocol() {
        String url = urlBase + "/third/xgxy/v1/getProtocolInfo";
        Map<String,String> params = new HashMap<>();
        params.put("amount", 1+"");
        params.put("nper", "2");
        params.put("userId","3F8FB2688A4E8FD568FC3DDBCAB46020");
        params.put("loanRemark","测试");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));

        System.out.println("request="+ JSON.toJSONString(params) + ", response=" + respResult);
    }


    @Test
    public void getRealPeriod() {
        String url = urlBase + "/third/xgxy/v1/getRealPeriod";
        Map<String,String> params = new HashMap<>();
        params.put("userId","tjc18637962344");
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
