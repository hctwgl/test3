package com.ald.fanbei.api.biz.bo;

/** 
 * @类描述:
 * @author fanmanfu 创建时间：2017年8月20日 下午8:07:41 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CollectionUpdateResqBo {

	private String code;
	private String msg;
	private String sign;
	private String data;
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "CollectionUpdateResqBo [code=" + code + ", msg=" + msg
				+ ", sign=" + sign + ", data=" + data + "]";
	}
	
	
}
