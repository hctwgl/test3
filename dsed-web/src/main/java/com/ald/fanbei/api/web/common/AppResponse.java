package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;



/**
 * 
 *@类描述：响应类
 *@author 陈金虎 2017年1月16日 下午11:54:56
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AppResponse extends AbstractSerial implements BaseResponse {
    
	private static final long serialVersionUID = -4560750757808462293L;
	private int                 code;
    private String              msg;
    private Object              data;
    
    public AppResponse(){
        
    }
    public AppResponse(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    
    public AppResponse(FanbeiExceptionCode excCode){
        this.code = excCode.getErrorCode();
        this.msg = excCode.getDesc();
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
