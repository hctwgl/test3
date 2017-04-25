/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年4月18日下午3:42:17
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAgentOrderDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long orderId;
	private Long userId;
	private String province;
	private String city;
	private String county;
	private String address;
	private String consignee;
	private String mobile;
	private String capture;
	private String remark;
	private String agentAccount;
	private String agentMessage;
	private Long agentId;
	private Date gmtAgentBuy;
	private Date gmtClosed;
	private String cancelReason;
	private String cancelDetail;
	private String closedReason;
	private String closedDetail;
	private Long matchOrderId;
	private Date gmtMatchOrder;
	private String goodsUrl;
	private String status; // 代买订单的状态
	
	

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the consignee
	 */
	public String getConsignee() {
		return consignee;
	}
	/**
	 * @param consignee the consignee to set
	 */
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the capture
	 */
	public String getCapture() {
		return capture;
	}
	/**
	 * @param capture the capture to set
	 */
	public void setCapture(String capture) {
		this.capture = capture;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return the agentAccount
	 */
	public String getAgentAccount() {
		return agentAccount;
	}
	/**
	 * @param agentAccount the agentAccount to set
	 */
	public void setAgentAccount(String agentAccount) {
		this.agentAccount = agentAccount;
	}
	/**
	 * @return the agentMessage
	 */
	public String getAgentMessage() {
		return agentMessage;
	}
	/**
	 * @param agentMessage the agentMessage to set
	 */
	public void setAgentMessage(String agentMessage) {
		this.agentMessage = agentMessage;
	}
	/**
	 * @return the agentId
	 */
	public Long getAgentId() {
		return agentId;
	}
	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	/**
	 * @return the gmtAgentBuy
	 */
	public Date getGmtAgentBuy() {
		return gmtAgentBuy;
	}
	/**
	 * @param gmtAgentBuy the gmtAgentBuy to set
	 */
	public void setGmtAgentBuy(Date gmtAgentBuy) {
		this.gmtAgentBuy = gmtAgentBuy;
	}
	/**
	 * @return the gmtClosed
	 */
	public Date getGmtClosed() {
		return gmtClosed;
	}
	/**
	 * @param gmtClosed the gmtClosed to set
	 */
	public void setGmtClosed(Date gmtClosed) {
		this.gmtClosed = gmtClosed;
	}
	/**
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}
	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	/**
	 * @return the cancelDetail
	 */
	public String getCancelDetail() {
		return cancelDetail;
	}
	/**
	 * @param cancelDetail the cancelDetail to set
	 */
	public void setCancelDetail(String cancelDetail) {
		this.cancelDetail = cancelDetail;
	}
	/**
	 * @return the closedReason
	 */
	public String getClosedReason() {
		return closedReason;
	}
	/**
	 * @param closedReason the closedReason to set
	 */
	public void setClosedReason(String closedReason) {
		this.closedReason = closedReason;
	}
	/**
	 * @return the closedDetail
	 */
	public String getClosedDetail() {
		return closedDetail;
	}
	/**
	 * @param closedDetail the closedDetail to set
	 */
	public void setClosedDetail(String closedDetail) {
		this.closedDetail = closedDetail;
	}
	/**
	 * @return the matchOrderId
	 */
	public Long getMatchOrderId() {
		return matchOrderId;
	}
	/**
	 * @param matchOrderId the matchOrderId to set
	 */
	public void setMatchOrderId(Long matchOrderId) {
		this.matchOrderId = matchOrderId;
	}
	/**
	 * @return the gmtMatchOrder
	 */
	public Date getGmtMatchOrder() {
		return gmtMatchOrder;
	}
	/**
	 * @param gmtMatchOrder the gmtMatchOrder to set
	 */
	public void setGmtMatchOrder(Date gmtMatchOrder) {
		this.gmtMatchOrder = gmtMatchOrder;
	}
	/**
	 * @return the goodsUrl
	 */
	public String getGoodsUrl() {
		return goodsUrl;
	}
	/**
	 * @param goodsUrl the goodsUrl to set
	 */
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
	
	

}
