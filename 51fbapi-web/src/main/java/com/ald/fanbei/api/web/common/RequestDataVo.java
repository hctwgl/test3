package com.ald.fanbei.api.web.common;

import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 *@类描述：请求对象
 *@author 陈金虎 2017年1月17日 上午12:21:48
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RequestDataVo extends AbstractSerial{
	private static final long serialVersionUID = 363386176803901540L;
	private String id;
    private String method;
    private Map<String,Object> system;
    private Map<String,Object> params;
    
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Map<String, Object> getSystem() {
        return system;
    }
    
    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
}
