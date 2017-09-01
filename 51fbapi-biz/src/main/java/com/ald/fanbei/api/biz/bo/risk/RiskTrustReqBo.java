package com.ald.fanbei.api.biz.bo.risk;

import java.util.HashMap;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 风控可信接口
 * @类描述:
 *
 * @auther caihuan 2017年8月31日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskTrustReqBo extends HashMap<String, String> {

	
	/**
	 * 订单号	
	 */
	private String orderNo;
	
	private String eventType;
	
	private String consumerNo;
	
	private String details;
	
	
	/**
	 * 签名信息
	 */
	private String signInfo;


	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
