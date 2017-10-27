package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：上树运营商请求bo
 *@author hexin 2017年3月24日 下午16:27:24
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskOperatorReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String orderNo;
	private String consumerNo;
	private String signInfo;
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	public String getConsumerNo() {
		return consumerNo;
	}
	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
}
