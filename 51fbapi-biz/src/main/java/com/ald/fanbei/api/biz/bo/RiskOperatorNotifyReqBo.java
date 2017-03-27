package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：上树运营商异步返回bo
 *@author hexin 2017年3月24日 下午16:27:24
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskOperatorNotifyReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String code;
	private String data;
	private String msg;
	private String signInfo;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}
	
}
