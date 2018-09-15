package com.ald.fanbei.api.biz.bo.xgxy;

import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;

public class XgxyReqBo extends HashMap<String, Object> {

    private static final long serialVersionUID = -5197302494589757587L;

    private String appId;  //三方商户唯一凭证
    private String sign;  //签名
    private Object data;  //加密参数
    private boolean success;
    private String  code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
        this.put("appId", appId);

    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
        this.put("sign", sign);

    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
        this.put("data", JSON.toJSONString(data));

    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
