package com.ald.fanbei.api.biz.third.util;


import static com.ald.fanbei.api.common.util.JsdSignUtil.generateSign;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.jsd.ResponseMessage;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("OriRateUtil")
public class OriRateUtil extends AbstractThird {
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);

    private static String url = null;

    private static String getXgxyUrl() {
        if (url == null) {
            url = ConfigProperties.get(Constants.CONFKEY_XGXY_HOST);
            return url;
        }
        return url;
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
            Map<String, Object> pay = new HashMap<>();
            pay.put("openId", openId);
            params.put("appId", "edspay");
            params.put("data", JsdSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(pay)), PRIVATE_KEY));
            params.put("sign", generateSign(JSONObject.parseObject(JSON.toJSONString(pay)), PRIVATE_KEY));
            String url = getXgxyUrl() + "/isp/open/third/eca/v1/getlayeredRate";
            String reqResult = "";
            if (url.contains("https")){
                reqResult = HttpUtil.doHttpsPostIgnoreCertJSON(url, JSON.toJSONString(params));
            }else {
                reqResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(params));
            }
            logThird(reqResult, url, JSON.toJSONString(pay));
            if (StringUtil.isBlank(reqResult)) {
                return null;
            }
            ResponseMessage responseMessage = JSONObject.parseObject(reqResult, ResponseMessage.class);
            if (Constants.XGXY_REQ_CODE_SUCC.equals(responseMessage.getCode().toString())) {
                return responseMessage.getData();
            }
        } catch (Exception e) {
            logger.info("rePayNoticeRequest request fail", e);
        }
        return null;
    }


}
