package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：风控审批bo
 *@author hexin 2017年3月20日 上午11:44:21
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskVerifyReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String orderNo;
	private String consumerNo;
	private String channel;
	private String scene;
	private String datas;
//	private String cardNo;//银行卡号
//	private String appName;	//应用名称 
//	private String ipAddress;	
//	private String blackBox;	
	private String reqExt;
	private String notifyUrl;
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
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
		this.put("channel", channel);
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
		this.put("scene", scene);
	}
	public String getDatas() {
		return datas;
	}
	public void setDatas(String datas) {
		this.datas = datas;
		this.put("datas", datas);
	}
	
	
	public String getReqExt() {
		return reqExt;
	}
	public void setReqExt(String reqExt) {
		this.reqExt = reqExt;
		this.put("reqExt", reqExt);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
}
