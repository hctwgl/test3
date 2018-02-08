package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：提额bo
 *@author Jiang Rongbo 2017年3月20日 上午11:44:21
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskQuotaReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String consumerNo;
	private String details;
	private String signInfo;
	
	public String getConsumerNo() {
		return consumerNo;
	}
	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}

}
