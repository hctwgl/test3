package com.ald.fanbei.api.common.util;

/**
 * @类描述：订单状态转换辅助类
 * @author chengkang 2017年7月25日上午11:31:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class StatusConvertUtil {
	private String code;
    private String statusMsg;
    private String statusRemark;
    
	public StatusConvertUtil() {
		super();
	}
	
	public StatusConvertUtil(String code, String statusMsg, String statusRemark) {
		super();
		this.code = code;
		this.statusMsg = statusMsg;
		this.statusRemark = statusRemark;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public String getStatusRemark() {
		return statusRemark;
	}
	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}
    
    
}
