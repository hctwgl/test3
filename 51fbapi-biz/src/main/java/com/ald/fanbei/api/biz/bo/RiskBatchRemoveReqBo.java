package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：用户批量迁移bo
 *@author hexin 2017年3月30日 下午14:34:28
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskBatchRemoveReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 912277311269987518L;
	private String orderNo;
	private String count;
	private String details;
	private String signInfo;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
		this.put("count", count);
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
		this.put("details", details);
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
}
