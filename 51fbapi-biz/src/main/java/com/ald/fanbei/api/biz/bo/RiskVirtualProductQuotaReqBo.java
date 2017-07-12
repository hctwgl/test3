package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述：虚拟商品额度查询接口
 * @author xiaotianjian 2017年7月6日下午3:34:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskVirtualProductQuotaReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 912277311269987518L;
	private String consumerNo;
	private String signInfo;
	private String details;
	/**
	 * @return the consumerNo
	 */
	public String getConsumerNo() {
		return consumerNo;
	}
	/**
	 * @param consumerNo the consumerNo to set
	 */
	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}
	/**
	 * @return the signInfo
	 */
	public String getSignInfo() {
		return signInfo;
	}
	/**
	 * @param signInfo the signInfo to set
	 */
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
