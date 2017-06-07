package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

/**
 *@类现描述：认证时强风控 用户信息同步bo
 *@author fmai 2017年06月06日 15:45
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskRegisterStrongReqBo extends HashMap<String, String> {

	private static final long serialVersionUID = 912277311269987519L;
	
	private String orderNo;
	private String consumerNo;
	private String event;
	private RiskUserInfoReqBo userInfo;
	private String directory;//通讯录
	private String linkManInfo;
	private String riskInfo;
	private String eventInfo;
	private String signInfo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
		this.put("event", event);
	}

	public RiskUserInfoReqBo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(RiskUserInfoReqBo userInfo) {
		this.userInfo = userInfo;
		this.put("userInfo", JSONObject.toJSONString(userInfo));
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
		this.put("directory", directory);
	}

	public String getLinkManInfo() {
		return linkManInfo;
	}

	public void setLinkManInfo(String linkManInfo) {
		this.linkManInfo = linkManInfo;
		this.put("linkManInfo", linkManInfo);
	}

	public String getRiskInfo() {
		return riskInfo;
	}

	public void setRiskInfo(String riskInfo) {
		this.riskInfo = riskInfo;
		this.put("riskInfo", riskInfo);
	}

	public String getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(String eventInfo) {
		this.eventInfo = eventInfo;
		this.put("eventInfo", eventInfo);
	}

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
	
	
}
