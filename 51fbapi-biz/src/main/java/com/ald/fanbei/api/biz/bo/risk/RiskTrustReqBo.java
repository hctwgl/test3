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
	 * 用户手机号
	 */
	private String phone;
	
	/**
	 * 同盾指纹
	 */
	private String blackBox;
	
	/**
	 * 手机硬件序列号
	 */
	private String imei;
	
	private String ip;
	
	/**
	 * 手机型号
	 */
	private String phoneType;
	
	/**
	 * 网络类型
	 */
	private String networkType;
	
	/**
	 * 网络类型
	 */
	private String osType;
	
	/**
	 * 签名信息
	 */
	private String signInfo;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBlackBox() {
		return blackBox;
	}

	public void setBlackBox(String blackBox) {
		this.blackBox = blackBox;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}
}
