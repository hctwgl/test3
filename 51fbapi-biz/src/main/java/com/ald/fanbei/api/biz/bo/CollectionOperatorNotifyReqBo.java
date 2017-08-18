package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：催收平台异步返回bo
 *@author chengkang 2017年8月3日 下午17:08:24
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CollectionOperatorNotifyReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;
	private String sign;
	private String timestamp;
	private String data;
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		this.put("data", data);
	}
	
}
