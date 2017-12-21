package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowLegalOrderRepaymentDo extends AbstractSerial {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long userId;
	private String name;
	private Long borrowId;
	private Long borrowLegalOrderCashId;
	private BigDecimal repayAmount;
	private BigDecimal actualAmount;
	private String tradeNo;
	private String tradeNoUps;
	private String tradeNoWx;
	private String tradeNoZfb;
	private Long userCouponId;
	private BigDecimal couponAmount;
	private BigDecimal rebateAmount;
	private String status;
	private String cardName;
	private String cardNo;
	private Date gmtCreate;
	private Date gmtModified;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getBorrowLegalOrderCashId() {
		return borrowLegalOrderCashId;
	}
	public void setBorrowLegalOrderCashId(Long borrowLegalOrderCashId) {
		this.borrowLegalOrderCashId = borrowLegalOrderCashId;
	}
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeNoUps() {
		return tradeNoUps;
	}
	public void setTradeNoUps(String tradeNoUps) {
		this.tradeNoUps = tradeNoUps;
	}
	public String getTradeNoWx() {
		return tradeNoWx;
	}
	public void setTradeNoWx(String tradeNoWx) {
		this.tradeNoWx = tradeNoWx;
	}
	public String getTradeNoZfb() {
		return tradeNoZfb;
	}
	public void setTradeNoZfb(String tradeNoZfb) {
		this.tradeNoZfb = tradeNoZfb;
	}
	public Long getUserCouponId() {
		return userCouponId;
	}
	public void setUserCouponId(Long userCouponId) {
		this.userCouponId = userCouponId;
	}
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBorrowId() {
		return borrowId;
	}
	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}
	
}