package com.ald.fanbei.api.biz.bo;

/**
 * 
 * @类描述：催收平台请求响应返呗结果
 * @author chengkang 2017年8月5日下午8:41:40
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CollectionSystemReqRespBo {

	private String code;
	private String msg;
	private String sign;
	private String timestamp;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
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
	@Override
	public String toString() {
		return "CollectionSystemReqRespBo [code=" + code + ", msg=" + msg
				+ ", sign=" + sign + ", timestamp=" + timestamp + "]";
	}
	
}
