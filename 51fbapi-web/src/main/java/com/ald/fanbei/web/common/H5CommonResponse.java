package com.ald.fanbei.web.common;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 *@类描述：CommonResponse
 *@author 陈金虎 2017年1月17日 下午6:14:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class H5CommonResponse {

	private Boolean           success;                                // 是否成功

    private String            msg;                                    // 返回提示

    private String            url;                                    // 跳转地址

    private Object            data;                                   // 返回数据

    private H5CommonResponse() {
    }

    public static H5CommonResponse getNewInstance() {
        return new H5CommonResponse();
    }

    public static H5CommonResponse getNewInstance(boolean success, String msg) {
        H5CommonResponse instance = new H5CommonResponse();
        instance.setSuccess(success);
        instance.setMsg(msg);
        return instance;
    }

    public static H5CommonResponse getNewInstance(boolean success, String msg, String url, Object data) {
        H5CommonResponse instance = new H5CommonResponse();
        instance.setSuccess(success);
        instance.setMsg(msg);
        instance.setUrl(url);
        instance.setData(data);
        return instance;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public String toString(){
    	return JSONObject.toJSONString(this);
    }

}
