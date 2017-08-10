package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：登陆信息
 * @author xiaotianjian 2017年8月10日下午5:28:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeGetDidiRiskInfoLoginInfoBo extends AbstractSerial {

	private static final long serialVersionUID = -2913860039060370990L;
	
	private Long time;//绑卡时间
	private String ip;//绑卡ip
	private String deviceid;//绑卡设备deviceid,安卓提供deviceid,IOS提供UUID
	private String source;//绑卡端 app   只有app
	private BigDecimal lat;//绑卡纬度
	private BigDecimal lng;//绑卡经度
	private String wifi_mac;//当不是wifi连接时, 为空
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the deviceid
	 */
	public String getDeviceid() {
		return deviceid;
	}
	/**
	 * @param deviceid the deviceid to set
	 */
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the lat
	 */
	public BigDecimal getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	/**
	 * @return the lng
	 */
	public BigDecimal getLng() {
		return lng;
	}
	/**
	 * @param lng the lng to set
	 */
	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}
	/**
	 * @return the wifi_mac
	 */
	public String getWifi_mac() {
		return wifi_mac;
	}
	/**
	 * @param wifi_mac the wifi_mac to set
	 */
	public void setWifi_mac(String wifi_mac) {
		this.wifi_mac = wifi_mac;
	}

	
	
	
}
