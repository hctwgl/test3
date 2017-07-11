package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年7月10日下午6:53:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskOverdueOrderBo extends HashMap<String, String>{

	private static final long serialVersionUID = -6100157119514768019L;
	
	private String orderNo;//订单编号
	private String details;//jsonArray 详情
	private String signInfo;//签名
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}
	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
		this.put("details", details);
	}
	
	
}
