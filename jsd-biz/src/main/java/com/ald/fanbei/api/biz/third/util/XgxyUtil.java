package com.ald.fanbei.api.biz.third.util;


import static com.ald.fanbei.api.common.util.JsdSignUtil.generateSign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyResqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DsedSignUtil;
import com.ald.fanbei.api.common.util.HttpUtilForXgxy;
import com.ald.fanbei.api.common.util.JsdAesUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("xgxyUtil")
public class XgxyUtil extends AbstractThird {
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);
    private static String APPID = ConfigProperties.get(Constants.CONFKEY_XGXY_APP_ID);
    public static final String XGXY_REQ_CODE_SUCC = "200";
    
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
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(params), dataStr);
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
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
            String dataStr = JSON.toJSONString(data);
            p.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
            p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
            p.put("appId", APPID);
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/repaymentNotify";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p), JSON.toJSONString(data));
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
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
            String dataStr = JSON.toJSONString(data);
            p.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
    		p.put("sign", generateSign(JSONObject.parseObject(dataStr),PRIVATE_KEY));
    		p.put("appId", APPID);
    		String url = getXgxyUrl() + "/isp/open/third/eca/v1/delayNotify";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p), JSON.toJSONString(data));
    		if (StringUtil.isBlank(reqResult)) {
    			return false;
    		}
    		XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
    		if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
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
            String dataStr = JSON.toJSONString(data);
            p.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
            p.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)),PRIVATE_KEY));
            p.put("appId", APPID);
            String url = getXgxyUrl()+"/isp/open/third/eca/v1/bandBankCardNotify";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(p), JSON.toJSONString(data));
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
                return true;
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }

        return false;

    }



    public String getUserContactsInfo(String openId) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("userId", openId);
            data.put("timestamp",System.currentTimeMillis()+"");
            String dataStr = JSON.toJSONString(data);

            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            params.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(dataStr), PRIVATE_KEY));

            String url = getXgxyUrl() + "/isp/open/third/edspay/v1/getAddressList";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(params), dataStr);
            if (StringUtil.isBlank(reqResult)) {
                return "";
            }
            XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
                return (String) resp.getData();
            }
        } catch (Exception e) {
            logger.info("overDueNoticeRequest request fail", e);
        }
        return "";
    }

    /**
     * 获取风控分层利率
     *
     * @param
     * @return
     */
    public String getOriRateNoticeRequest(String openId) {

        try {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            data.put("openId", openId);
            data.put("timestamp",System.currentTimeMillis()+"");
            String dataStr = JSON.toJSONString(data);

            params.put("appId", XgxyUtil.APPID);
            params.put("data", JsdAesUtil.encryptToBase64Third(dataStr, PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(dataStr), PRIVATE_KEY));
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/getlayeredRate";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(params), dataStr);
            if (StringUtil.isBlank(reqResult)) {
                return null;
            }
            XgxyResqBo resp = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(resp.getCode())) {
            	JSONObject dataObj = JSON.parseObject(resp.getData());
                return dataObj.getString("interestNewRate");
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }
        return null;
    }

    /**
     * 获取借款信息(搭售商品信息和借款经纬度)
     *
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
	public HashMap<String,String> borrowNoticeRequest(Map<String,String> data) {
        HashMap<String,String> param = new HashMap<>();
        try {
            logger.info("borrowNoticeRequest to xgxy request start");
            data.put("timestamp",String.valueOf(new Date().getTime()));
            String dataStr = JSON.toJSONString(data);
            Map<String, Object> params = new HashMap<>();
            params.put("appId", APPID);
            params.put("data", DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/borrowOrder";
            String reqResult = HttpUtilForXgxy.post(url, JSON.toJSONString(params), dataStr);
            if (StringUtil.isBlank(reqResult)) {
                return param;
            }
            XgxyResqBo overdueReqBo1 = JSONObject.parseObject(reqResult, XgxyResqBo.class);
            if (XGXY_REQ_CODE_SUCC.equals(overdueReqBo1.getCode())) {
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
