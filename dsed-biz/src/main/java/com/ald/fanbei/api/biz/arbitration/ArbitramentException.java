package com.ald.fanbei.api.biz.arbitration;

/**
 * 自定义异常类
 * @Title：ArbitramentException.java
 * @Author：wangyihui
 * @Date：2017年6月29日下午4:23:55
 * @Description：
 */
public class ArbitramentException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642910503527783291L;
	
	//错误码
	public String err_code;
	//错误信息
	public String err_msg;

	public ArbitramentException(){}
	
	public ArbitramentException(String err_msg){
		this.err_msg = err_msg;
	}
	
	public ArbitramentException(String err_code, String err_msg){
		this.err_code = err_code;
		this.err_msg = err_msg;
	}

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
}
