package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述：风控逾期订单查询
 * @author xiaotianjian 2017年7月10日下午3:30:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskQueryOverdueOrderReqBo extends HashMap<String, String>{
	
	private static final long serialVersionUID = 6498853715603054158L;
	
	private String consumerNo;
	private String signInfo;
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
	
}
