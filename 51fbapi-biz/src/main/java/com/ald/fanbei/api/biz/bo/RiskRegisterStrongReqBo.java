package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSONObject;

public abstract class RiskRegisterStrongReqBo extends HashMap<String, String> {

	private static final long serialVersionUID = 912277311269987519L;
	private String orderNo = "";
	private String consumerNo = "";
	private String event = "";
	private String userInfo = "";
	private String directory = "";// 通讯录
	private String linkManInfo = "";
	private String riskInfo = "";
	private String eventInfo = "";
	private String signInfo = "";
	
	public RiskRegisterStrongReqBo(String consumerNo,  String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String NotifyHost){
		create(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, NotifyHost);
	}

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

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
		this.put("event", event);
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
		this.put("userInfo", userInfo);
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

	protected abstract void create(String consumerNo,  String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String NotifyHost);

	public String transferRisk() {
		return JSONObject.toJSONString(this);
	}

}
