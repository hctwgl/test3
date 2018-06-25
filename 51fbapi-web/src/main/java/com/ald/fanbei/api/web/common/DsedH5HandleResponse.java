package com.ald.fanbei.api.web.common;

import com.alibaba.fastjson.JSONObject;

/**
 * @author 江荣波 2018年1月10日 下午6:14:32
 * @类描述：H5HandleResponse
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DsedH5HandleResponse implements BaseResponse {

    private int code;                                // 是否成功

    private String message;                                    // 返回提示

    private Object data;                                   // 返回数据

    private DsedH5HandleResponse() {
    }

    public DsedH5HandleResponse(int code, String msg, Object data) {
        getNewInstance(code, msg, data);
    }

    public DsedH5HandleResponse(int code, String msg) {
        getNewInstance(code, msg);
    }


    public static DsedH5HandleResponse getNewInstance() {
        return new DsedH5HandleResponse();
    }

    public static DsedH5HandleResponse getNewInstance(int code, String msg) {
        DsedH5HandleResponse instance = new DsedH5HandleResponse();
        instance.setCode(code);
        instance.setMsg(msg);
        return instance;
    }

    public static DsedH5HandleResponse getNewInstance(int code, String msg, Object data) {
        DsedH5HandleResponse instance = new DsedH5HandleResponse();
        instance.setCode(code);
        instance.setMsg(msg);
        instance.setData(data);
        return instance;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
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
