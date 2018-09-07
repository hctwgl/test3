package com.ald.fanbei.api.biz.third.util;


import static com.ald.fanbei.api.common.util.JsdSignUtil.generateSign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyOverdueReqBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyPayBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyPayReqBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsdAesUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("XgxyUtil")
public class XgxyUtil extends AbstractThird {
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);

    public final static String APPID = "UJ3331";
    private static String url = null;

    private static String getXgxyUrl() {
        if (url == null) {
            url = ConfigProperties.get(Constants.CONFKEY_XGXY_HOST);
            return url;
        }
        return url;
    }

    /**
     * 打款通知请求
     *
     * @param
     * @return
     */
    public boolean payNoticeRequest(XgxyPayBo payBo) {
        try {
        	String dataStr = JSON.toJSONString(payBo);
            
            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            params.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
            params.put("sign", generateSign(JSON.parseObject(dataStr), PRIVATE_KEY));
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/borrowStatusNotify";
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(params));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(params));
            }
            logThird(reqResult, url, payBo);
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyPayReqBo payRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(payRespResult.get("code"))) {
                return true;
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }

        return false;
    }

    /**
     * 逾期通知请求
     *
     * @param data
     * @return
     */
    public boolean overDueNoticeRequest(Map<String,String> data) {
        try {
            logger.info("overDueNoticeRequest to xgxy request start");
            JSONObject overdue = new JSONObject();
            overdue.put("borrowNo", data.get("borrowNo"));
            overdue.put("overdueDays", data.get("overdueDays"));
            overdue.put("period", data.get("curPeriod"));
            overdue.put("tradeNo", data.get("tradeNo"));
            
            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            params.put("data", JsdAesUtil.encryptToBase64Third(overdue.toJSONString(), PRIVATE_KEY));
            params.put("sign", generateSign(overdue, PRIVATE_KEY));
            params.put("timestamp",System.currentTimeMillis()+"");
            
            String url = getXgxyUrl() + "/isp/open/third/edspay/v1/giveBackOverdueResult";
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(params));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(params));
            }
            logThird(reqResult, url, overdue);
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyOverdueReqBo overdueReqBo1 = JSONObject.parseObject(reqResult, XgxyOverdueReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(overdueReqBo1.get("code"))) {
                return true;
            }
        } catch (Exception e) {
            logger.info("overDueNoticeRequest to xgxy request fail", e);
        }
        return false;

    }


    /**
     * 还款通知请求
     *
     * @param data
     * @return
     */
    public boolean dsedRePayNoticeRequest(HashMap<String, String> data) {
        try {
            logger.info("dsedRePayNoticeRequest start data = "+data);
            Map<String, String> p = new HashMap<>();
            p.put("data", JsdAesUtil.encryptToBase64Third(JSON.toJSONString(data), PRIVATE_KEY));
            p.put("sign", generateSign(JSON.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            p.put("appId", APPID);
            p.put("timestamp",System.currentTimeMillis()+"");
            String url = getXgxyUrl()+"/isp/open/third/eca/v1/repaymentNotify";
            logger.info("data = " + data +",url = " +url );
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(p));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
            }
            logThird(reqResult, url, data);
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(rePayRespResult.get("code"))) {
                return true;
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }

        return false;

    }
    
    /**
     * 续期通知请求
     *
     * @param data
     * @return
     */
    public boolean jsdRenewalNoticeRequest(HashMap<String, String> data) {
    	try {
    		logger.info("jsdRenewalNoticeRequest start data = "+data);
    		Map<String, String> p = new HashMap<>();
    		p.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
    		p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
    		p.put("appId", APPID);
    		String url = getXgxyUrl() + "/isp/open/third/eca/v1/delayNotify";
//    		String url = "http://192.168.156.103:1112/isp/open/third/eca/v1/delayNotify";
    		logger.info("data = " + data +",url = " +url );
    		String reqResult = "";
    		if (url.contains("https")){
    			reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(p));
    		}else {
    			reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
    		}
    		logThird(reqResult, url, data);
    		if (StringUtil.isBlank(reqResult)) {
    			return false;
    		}
    		XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
    		if (Constants.XGXY_REQ_CODE_SUCC.equals(rePayRespResult.get("code"))) {
    			return true;
    		}
    	} catch (Exception e) {
    		logger.info("renewalNoticeRequest request fail", e);
    	}
    	
    	return false;
    	
    }


    /**
     * 绑卡通知
     *
     * @param data
     * @return
     */
    public boolean bindBackNoticeRequest(HashMap<String, String> data) {
        try {
            logger.info("bindBackNoticeRequest start data = "+data);
            Map<String, String> p = new HashMap<>();
            p.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
            p.put("appId", APPID);
            String url = getXgxyUrl()+"/isp/open/third/eca/v1/bandBankCardNotify";
            logger.info("data = " + data +",url = " +url );
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(p));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(p));
            }
            logThird(reqResult, url, data);
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(rePayRespResult.get("code"))) {
                return true;
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }

        return false;

    }



    public String getUserContactsInfo(String openId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            Map<String, String> data = new HashMap<>();
            data.put("userId", openId);
            params.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            
            String url = getXgxyUrl() + "/isp/open/third/edspay/v1/getAddressList";
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(params));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(params));
            }
            logThird(reqResult, url, data);
            if (StringUtil.isBlank(reqResult)) {
                return "";
            }
            XgxyReqBo reqBo = JSONObject.parseObject(reqResult, XgxyReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(reqBo.get("code"))) {
                return (String) reqBo.get("data");
            }
        } catch (Exception e) {
            logger.info("overDueNoticeRequest request fail", e);
        }
        return "";
    }


    public static void main(String[] ars) {
//        XgxyUtil xgxyUtil = new XgxyUtil();
//        xgxyUtil.getUserContactsInfo("edspay21");
        HashMap<String,String> cardMap=new HashMap<>();
        cardMap.put("openId", "EB56E1F0A9383508DB8FD039C7D37BD1");
        cardMap.put("bindNo", "bank0904eca291900000012");
        cardMap.put("status", "Y");
        cardMap.put("timestamp",System.currentTimeMillis()+"");
		XgxyUtil xgxyUtil = new XgxyUtil();
		System.out.println(xgxyUtil.bindBackNoticeRequest(cardMap));
		
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String pres = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (params.get(key) != null) {
                String value = (String) params.get(key);
                pres = pres + value;
            }
        }

        return pres;
    }


}
