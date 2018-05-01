package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;


/**
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年2月28日上午10:29:00
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class AfBorrowCashDto extends AfBorrowCashDo{

	private static final long serialVersionUID = -662078081699916696L;
	
	//实时推送的债权相关信息
	private String orderNo;//借款订单号
	private Long userId;//借款人Id
	private String name;//借款人姓名
	private String cardId;//借款人身份证号
	private String mobile;//借款人手机号码
	private String bankNo;//银行卡号
	private String cardName;//银行卡所属行
	private BigDecimal money;//用户实际到账金额
	private String type;//借款类型
	private Date loanStartTime;//借款开始时间戳（单位：秒）
    private String borrowRemark;//借款用途
    private String refundRemark;//还款来源
	private Long recycleId;

	public Long getRecycleId() {
		return recycleId;
	}

	public void setRecycleId(Long recycleId) {
		this.recycleId = recycleId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getLoanStartTime() {
		return loanStartTime;
	}

	public void setLoanStartTime(Date loanStartTime) {
		this.loanStartTime = loanStartTime;
	}

	public String getBorrowRemark() {
		return borrowRemark;
	}

	public void setBorrowRemark(String borrowRemark) {
		this.borrowRemark = borrowRemark;
	}

	public String getRefundRemark() {
		return refundRemark;
	}

	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
}
