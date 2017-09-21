package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：绑定卡信息
 *@author xiaotianjian 2017年8月10日下午5:19:55
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeGetDidiRiskInfoCardInfoBo extends AbstractSerial {

	private static final long serialVersionUID = -2913860039060370990L;
	
	private String card_id;//用户绑定的银行卡号（可加密） 通过MD5加密
	private String people_id;//身份证号（可加密） 通过MD5加密
	private Long time;//绑卡时间
	private String ip;//绑卡ip
	private String deviceid;//绑卡设备deviceid,安卓提供deviceid,IOS提供UUID
	private BigDecimal lat;//绑卡纬度
	private BigDecimal lng;//绑卡经度
	private String status;//1 绑卡成功 0 失败,当微信支付时, 绑卡为0
	private String channel;//绑卡渠道wechat,微信  当微信支付时用 debitcard 借记卡 当银行卡以及额度支付时使用
	private String source;//绑卡端 app   只有app
	private String wifi_mac;//当不是wifi连接时, 为空
	/**
	 * @return the card_id
	 */
	public String getCard_id() {
		return card_id;
	}
	/**
	 * @param card_id the card_id to set
	 */
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	/**
	 * @return the people_id
	 */
	public String getPeople_id() {
		return people_id;
	}
	/**
	 * @param people_id the people_id to set
	 */
	public void setPeople_id(String people_id) {
		this.people_id = people_id;
	}
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}
	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
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
