package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月29日下午8:46:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomePushPayRequestBo extends HashMap<String, String> {
	
	private static final long serialVersionUID = -4326788124985759843L;
	
	private String orderId;//订单Id
	private Long userId;//51返呗用户id
	private BigDecimal amount;//成功支付金额
	private String status;//推送状态 
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
		this.put(BoluomeCore.ORDER_ID, orderId);
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
		this.put(BoluomeCore.USER_ID, userId+StringUtils.EMPTY);
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		this.put(BoluomeCore.AMOUNT, amount+StringUtils.EMPTY);
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
		this.put(BoluomeCore.STATUS, status);
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
		this.put(BoluomeCore.TIME_STAMP, timestamp + StringUtils.EMPTY);
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
		this.put(BoluomeCore.SIGN, sign);
	}
}
