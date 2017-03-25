package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月24日下午4:58:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeOrderBo extends AbstractSerial {
	
	private static final long serialVersionUID = 7261726855615445924L;
	
	private String orderId;//订单Id
	private String orderType;//订单类型（dianying：电影，外卖：waimai等）
	private String orderTitle;//订单标题
	private String userId;//用户id
	private String userPhone;//用户手机号
	private BigDecimal price;//支付价格\对于先服务后付钱的情况，下单时无法获取支付价格
	private Integer status;//订单状态
	private String displayStatus;//订单状态字面值
	private Integer createdTime;//订单创建时刻时间戳（毫秒）
	private Integer expiredTime;//待支付订单--支付超时剩余时间（毫秒）
	private String detailUrl;//订单详情H5页面链接
	private Long timestamp;//签名时间戳
	private String sign;//签名参数
	
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}
	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	/**
	 * @return the orderTitle
	 */
	public String getOrderTitle() {
		return orderTitle;
	}
	/**
	 * @param orderTitle the orderTitle to set
	 */
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the userPhone
	 */
	public String getUserPhone() {
		return userPhone;
	}
	/**
	 * @param userPhone the userPhone to set
	 */
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the displayStatus
	 */
	public String getDisplayStatus() {
		return displayStatus;
	}
	/**
	 * @param displayStatus the displayStatus to set
	 */
	public void setDisplayStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}
	/**
	 * @return the createdTime
	 */
	public Integer getCreatedTime() {
		return createdTime;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Integer createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * @return the expiredTime
	 */
	public Integer getExpiredTime() {
		return expiredTime;
	}
	/**
	 * @param expiredTime the expiredTime to set
	 */
	public void setExpiredTime(Integer expiredTime) {
		this.expiredTime = expiredTime;
	}
	/**
	 * @return the detailUrl
	 */
	public String getDetailUrl() {
		return detailUrl;
	}
	/**
	 * @param detailUrl the detailUrl to set
	 */
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
	
}
