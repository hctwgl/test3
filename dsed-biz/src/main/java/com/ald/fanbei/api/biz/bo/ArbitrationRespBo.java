package com.ald.fanbei.api.biz.bo;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.util.HashMap;

/**
 * @类描述：在线仲裁系统响应类
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 上午10:59:03
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class ArbitrationRespBo extends HashMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -285055279023601217L;
	
	private String errCode;		//错误码
	private String errMsg;		//错误信息
	private Object result;		//返回结果
	
	
	
	public ArbitrationRespBo() {
		super();
	}

	public ArbitrationRespBo(String errCode, String errMsg, String result) {
		super();
		this.errCode = errCode;
		this.errMsg = errMsg;
		this.result = result;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
		this.put("errCode", errCode);
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
		this.put("errMsg", errMsg);
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
		this.put("result", result);
	}


}
