package com.ald.fanbei.api.biz.third.util;


import static com.ald.fanbei.api.common.util.JsdSignUtil.generateSign;

import java.util.*;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyOverdueReqBo;
import com.ald.fanbei.api.common.util.*;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyPayReqBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("XgxyUtil")
public class XgxyUtil extends AbstractThird {
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);

    public final static String APPID = "speedloan";
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
    public boolean borrowNoticeRequest(XgxyBorrowNoticeBo borrowNoticeBo) {
        try {
            borrowNoticeBo.setTimestamp(new Date().getTime());
        	String dataStr = JSON.toJSONString(borrowNoticeBo);
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
            logThird(reqResult, url, borrowNoticeBo);
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
     * 还款通知请求
     *
     * @param
     * @return
     */
    public boolean repayNoticeRequest(HashMap<String, String> data) {
        try {
            Map<String, String> p = new HashMap<>();
            data.put("timestamp",String.valueOf(new Date().getTime()));
            p.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
            p.put("appId", APPID);
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/repaymentNotify";
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
     * 续期通知请求
     *
     * @param data
     * @return
     */
    public boolean jsdRenewalNoticeRequest(HashMap<String, String> data) {
    	try {
    		Map<String, String> p = new HashMap<>();
            data.put("timestamp",String.valueOf(new Date().getTime()));
    		p.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
    		p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
    		p.put("appId", APPID);
    		String url = getXgxyUrl() + "/isp/open/third/eca/v1/delayNotify";
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
            Map<String, String> p = new HashMap<>();
            data.put("timestamp",String.valueOf(new Date().getTime()));
            p.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
            p.put("appId", APPID);
            String url = getXgxyUrl()+"/isp/open/third/eca/v1/bandBankCardNotify";
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


    /**
     * 获取借款信息(搭售商品信息和借款经纬度)
     *
     * @param data
     * @return
     */
    public HashMap<String,String> borrowNoticeRequest(Map<String,String> data) {
        HashMap<String,String> param = new HashMap<>();
        try {
            logger.info("borrowNoticeRequest to xgxy request start");
            data.put("timestamp",String.valueOf(new Date().getTime()));
            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            params.put("data", DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
//            String url = getXgxyUrl() + "/isp/open/third/eca/v1/borrowOrder";
            String url = "http://192.168.156.59:1112/isp/open/third/eca/v1/borrowOrder";
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(params));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(params));
            }
            logThird(reqResult, url, JSON.toJSONString(data));
            if (StringUtil.isBlank(reqResult)) {
                return param;
            }
            XgxyOverdueReqBo overdueReqBo1 = JSONObject.parseObject(reqResult, XgxyOverdueReqBo.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(overdueReqBo1.get("code"))) {
                JSONObject jsonObject = JSON.parseObject(reqResult);
                return JSONObject.parseObject(jsonObject.get("data").toString(),HashMap.class);
            }
        } catch (Exception e) {
            logger.info("bindBankNoticeRequest to xgxy request fail", e);
        }
        return param;
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
