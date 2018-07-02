package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
import java.util.Map;

/**
 *  
 * @类描述：致诚阿福请求数据父节点类
 * @author chenxuankai 2017年11月23日12:38:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ParamsFather implements Serializable {
	
	private String errorCode;
	private String message;
	private String params;
	
	public ParamsFather(String errorCode, String message, String params) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.params = params;
	}

	public ParamsFather() {}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}	
	
}
