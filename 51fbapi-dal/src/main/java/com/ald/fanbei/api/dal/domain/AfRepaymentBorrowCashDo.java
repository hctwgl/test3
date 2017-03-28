/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月27日下午8:52:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRepaymentBorrowCashDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;
	private String repayNo;
	private BigDecimal repaymentAmount;
	private BigDecimal actualAmount;
	private Long borrowId;
	private String payTradeNo;
	private String tradeNo;
	private Long userCouponId;
	private BigDecimal couponAmount;
	private BigDecimal rebateAmount;
	private String status;
	private Long userId;
	private String cardNumber;
	private String cardName;

	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the repayNo
	 */
	public String getRepayNo() {
		return repayNo;
	}
	/**
	 * @param repayNo the repayNo to set
	 */
	public void setRepayNo(String repayNo) {
		this.repayNo = repayNo;
	}
	/**
	 * @return the repaymentAmount
	 */
	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}
	/**
	 * @param repaymentAmount the repaymentAmount to set
	 */
	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	/**
	 * @return the actualAmount
	 */
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	/**
	 * @param actualAmount the actualAmount to set
	 */
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	/**
	 * @return the borrowId
	 */
	public Long getBorrowId() {
		return borrowId;
	}
	/**
	 * @param borrowId the borrowId to set
	 */
	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}
	/**
	 * @return the payTradeNo
	 */
	public String getPayTradeNo() {
		return payTradeNo;
	}
	/**
	 * @param payTradeNo the payTradeNo to set
	 */
	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}
	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}
	/**
	 * @param tradeNo the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	/**
	 * @return the userCouponId
	 */
	public Long getUserCouponId() {
		return userCouponId;
	}
	/**
	 * @param userCouponId the userCouponId to set
	 */
	public void setUserCouponId(Long userCouponId) {
		this.userCouponId = userCouponId;
	}
	/**
	 * @return the couponAmount
	 */
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	/**
	 * @param couponAmount the couponAmount to set
	 */
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	/**
	 * @return the rebateAmount
	 */
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	/**
	 * @param rebateAmount the rebateAmount to set
	 */
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
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
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	/**
	 * @return the cardName
	 */
	public String getCardName() {
		return cardName;
	}
	/**
	 * @param cardName the cardName to set
	 */
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
	
	
}
