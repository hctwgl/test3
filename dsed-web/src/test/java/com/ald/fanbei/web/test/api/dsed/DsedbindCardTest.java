package com.ald.fanbei.web.test.api.dsed;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DsedSignUtil;
import com.ald.fanbei.web.test.common.BaseTest;
import com.ald.fanbei.web.test.common.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class DsedbindCardTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//	String urlBase = "https://testapi.51fanbei.com";
    String urlBase = "http://localhost:8080";
    //	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
    String userName = "15669066271";    //田建成 cardId:3111464125 支付密码123456
//	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
//	String userName = "13370127054";	//王卿 	支付密码123456
//	String userName = "13656648524";	//朱玲玲 支付密码123456
//	String userName = "13510301615";	//王绪武 支付密码123456
//	String userName = "17756648524";	//新账号 支付密码123456

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
    public void dsedApplyBind() {
        String url = urlBase + "/third/xgxy/v1/getAddressList";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "10,12");

        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
         String respResult = com.ald.fanbei.api.common.util.HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));

    }


    @Test
    public void getSms() {
        String url = urlBase+ "/third/xgxy/v1/getSmsCode";
        Map<String, String> params = new HashMap<>();
        params.put("busiFlag", "3111465143");
        params.put("type", "BIND");
        params.put("userId","19428E8AA37E288F9A4166C93A75E403");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = com.ald.fanbei.api.common.util.HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
    }
    @Test
    public void dsedSubmitBind() {
        String url = urlBase + "/third/xgxy/v1/smsCodeSubmit";
        Map<String, String> params = new HashMap<>();
        params.put("busiFlag", "3111465113");
        params.put("code", "247581");
        params.put("userId","edspay22");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = com.ald.fanbei.api.common.util.HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
    }
    @Test
    public void getContacts() {
        String url = urlBase + "/third/xgxy/v1/bankCardBind";
        Map<String, String> params = new HashMap<>();
        params.put("userId","13989455823");
        String data = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)),"aef5c8c6114b8d6a");
        Map<String, String> p = new HashMap<>();
        p.put("data", data);
        p.put("sign", generateSign(params, "aef5c8c6114b8d6a"));
        String respResult = com.ald.fanbei.api.common.util.HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
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


    /**
     * 贷款申请成功后，模拟 UPS 回调 返呗API
     */
    @Test
    public void delegatePay() {
        String url = urlBase + "/third/ups/delegatePay?";
        String orderNo = "01dpay23425234dfssdfs";
        String merPriv = UserAccountLogType.LOAN.getCode();
        String tradeState = "00";
        String reqExt = "154";

        String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeState=" + tradeState + "&reqExt=" + reqExt;
        url += reqStr;

        testH5(url, null, userName, true);
    }





    @Test
    public void collect() {
        String url = urlBase + "/third/ups/collect?";
        String orderNo = "hq2018051510143800802";
        String merPriv = PayOrderSource.REPAY_LOAN.getCode();
        String tradeNo = "xianFenghq2018051510143800802";
        String tradeState = "00";

        String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeNo=" + tradeNo + "&tradeState=" + tradeState;
        url += reqStr;
        Map<String, String> params = new HashMap<>();
        testApi(url, params, userName, true);
    }

    @Test
    public void offlineRepayment() throws UnsupportedEncodingException {

    }

}
