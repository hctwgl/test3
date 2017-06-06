package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 *@类现描述：用户信息
 *@author fmai 2017年6月6日 15:58
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskUserInfoReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 912272017269987518L;
	private String realName;
	private String phone;
	private String idNo;
	private String email;
	private String qq;
	private String alipayNo;
	private String openId;
	private String address;
	private String channel;
	private String reqExt;
	
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
		this.put("realName", realName);
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
		this.put("phone", phone);
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
		this.put("idNo", idNo);
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
		this.put("email", email);
	}
	public String getAlipayNo() {
		return alipayNo;
	}
	public void setAlipayNo(String alipayNo) {
		this.alipayNo = alipayNo;
		this.put("alipayNo", alipayNo);
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
		this.put("openId", openId);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		this.put("address", address);
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
		this.put("channel", channel);
	}
	public String getReqExt() {
		return reqExt;
	}
	public void setReqExt(String reqExt) {
		this.reqExt = reqExt;
		this.put("reqExt", reqExt);
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
		this.put("qq", qq);
	}
}
