package com.ald.fanbei.api.biz.third.util;


import static com.ald.fanbei.api.common.util.DsedSignUtil.generateSign;
import static com.ald.fanbei.api.common.util.HttpUtil.doHttpPostJsonParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.bo.XgxyOverdueReqBo;
import com.ald.fanbei.api.biz.bo.XgxyPayBo;
import com.ald.fanbei.api.biz.bo.XgxyPayReqBo;
import com.ald.fanbei.api.biz.bo.XgxyReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DsedSignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("XgxyUtil")
public class XgxyUtil  extends AbstractThird {
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);
    
    private static String url = null;
    
    private static String getXgxyUrl() {
        if (url == null) {
        	url = ConfigProperties.get(Constants.CONFKEY_XGXY_URL);
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
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> pay = new HashMap<>();
            pay.put("borrowNo", payBo.getBorrowNo());
            pay.put("status", payBo.getStatus());
            if ("PAYSUCCESS".equals(payBo.getStatus())) {
                pay.put("gmtArrival", payBo.getGmtArrival());
                pay.put("tradeNo", payBo.getTrade());
            } else {
                pay.put("reason", payBo.getReason());
            }
            params.put("appId", "edspay");
            params.put("data", DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(pay)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(pay)), PRIVATE_KEY));
            String reqResult = doHttpPostJsonParam(getXgxyUrl() + "/open/third/edspay/v1/giveBackPayResult", JSON.toJSONString(params));
            logThird(reqResult, "giveBackPayResult", JSON.toJSONString(pay));
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyPayReqBo payRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
            if (Constants.XGXY_REQ_CODE .equals(payRespResult.get("code"))) {
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
     * @param overdueBo
     * @return
     */
    public boolean overDueNoticeRequest(XgxyOverdueBo overdueBo) {
        try {
            logger.info("overDueNoticeRequest request start");
            Map<String, Object> params = new HashMap<>();
            params.put("appId", "edspay");
            Map<String, String> overdue = new HashMap<>();
            overdue.put("borrowNo", overdueBo.getBorrowNo());
            overdue.put("overdueDays", String.valueOf(overdueBo.getOverdueDays()));
            overdue.put("curPeriod", overdueBo.getCurPeriod());
            params.put("data", DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(overdue)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(overdue)), PRIVATE_KEY));
            String reqResult = doHttpPostJsonParam(getXgxyUrl() + "/open/third/edspay/v1/giveBackOverdueResult", JSON.toJSONString(params));
            logThird(reqResult, "giveBackOverdueResult", JSON.toJSONString(overdue));
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyOverdueReqBo overdueReqBo1 = JSONObject.parseObject(reqResult, XgxyOverdueReqBo.class);
            if (Constants.XGXY_REQ_CODE .equals(overdueReqBo1.get("code"))) {
                return true;
            }
        } catch (Exception e) {
            logger.info("overDueNoticeRequest request fail", e);
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
            String oriParamJson = JSON.toJSONString(data);
            JSONObject paramJsonObject = JSONObject.parseObject(oriParamJson);
            String data1 = DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY);
            Map<String, String> p = new HashMap<>();
            p.put("data", data1);
            p.put("sign", generateSign(paramJsonObject, PRIVATE_KEY));
            p.put("appId", "edspay");
            String reqResult = doHttpPostJsonParam(getXgxyUrl() + "/open/third/edspay/v1/giveBackRepayResult", JSON.toJSONString(p));
            logThird(reqResult, "giveBackRepayResult", JSON.toJSONString(data));
            if (StringUtil.isBlank(reqResult)) {
                return false;
            }
            XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult, XgxyPayReqBo.class);
            if (Constants.XGXY_REQ_CODE .equals(rePayRespResult.get("code")) ) {
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
            params.put("appId", "edspay");
            Map<String, String> data = new HashMap<>();
            data.put("userId", openId);
            params.put("data", DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(data)), PRIVATE_KEY));
            String reqResult = doHttpPostJsonParam(getXgxyUrl() + "/open/third/edspay/v1/getAddressList", JSON.toJSONString(params));
            logThird(reqResult, "getAddressList", JSON.toJSONString(data));
            if (StringUtil.isBlank(reqResult)) {
                return "";
            }
            XgxyReqBo reqBo = JSONObject.parseObject(reqResult, XgxyReqBo.class);
            if (Constants.XGXY_REQ_CODE .equals(reqBo.get("code"))) {
                return (String) reqBo.get("data");
            }
        } catch (Exception e) {
            logger.info("overDueNoticeRequest request fail", e);
        }
        return "";
    }


    public static void main(String[] ars) {
        XgxyUtil xgxyUtil = new XgxyUtil();
        xgxyUtil.getUserContactsInfo("edspay21");

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
