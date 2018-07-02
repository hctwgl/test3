package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * @类现描述：风控审批bo
 * @author hexin 2017年3月20日 上午11:44:21
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskRaiseQuotaReqBo extends HashMap<String, String> {

	private static final long serialVersionUID = 4608466453298742176L;

	private String orderNo;
	private String eventType;
	private String consumerNo;
	private String scene;
	private String details;
	// private String amount;
	// private String income;
	// private String overdueDays;
	// private String borrowCount;
	private String reqExt;
	private String signInfo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
		this.put("eventType", eventType);
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
		this.put("scene", scene);
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
		this.put("details", details);
	}

	public String getReqExt() {
		return reqExt;
	}

	public void setReqExt(String reqExt) {
		this.reqExt = reqExt;
		this.put("reqExt", reqExt);
	}

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}

}
