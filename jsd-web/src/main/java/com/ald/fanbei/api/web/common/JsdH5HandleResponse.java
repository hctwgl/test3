package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 江荣波 2018年1月10日 下午6:14:32
 * @类描述：H5HandleResponse
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JsdH5HandleResponse {

    private int code;                                // 是否成功

    private String message;                                    // 返回提示

    private Object data;                                   // 返回数据

    private JsdH5HandleResponse() {
    }

    public JsdH5HandleResponse(int importCode, String importMsg, Object importData) {
        code = importCode;
        message = importMsg;
        data = importData;
    }

    public JsdH5HandleResponse(BizExceptionCode excCode) {
        code = excCode.getErrorCode();
        message = excCode.getDesc();
        data = "";
    }

    public JsdH5HandleResponse(BizExceptionCode excCode,String importMsg) {
        code = excCode.getErrorCode();
        message = importMsg;
        data = "";
    }

    public JsdH5HandleResponse(int importCode, String importMsg) {
        code = importCode;
        message = importMsg;
    }


    public static JsdH5HandleResponse getNewInstance() {
        return new JsdH5HandleResponse();
    }

    public static JsdH5HandleResponse getNewInstance(int code, String msg) {
        JsdH5HandleResponse instance = new JsdH5HandleResponse();
        instance.setCode(code);
        instance.setMessage(msg);
        return instance;
    }

    public static JsdH5HandleResponse getNewInstance(int code, String msg, Object data) {
        JsdH5HandleResponse instance = new JsdH5HandleResponse();
        instance.setCode(code);
        instance.setMessage(msg);
        instance.setData(data);
        return instance;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }


}
