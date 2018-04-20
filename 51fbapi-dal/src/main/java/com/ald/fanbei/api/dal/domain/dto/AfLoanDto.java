/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;


import java.math.BigDecimal;
import java.sql.Date;

import com.ald.fanbei.api.dal.domain.AfLoanDo;

/**
 * 
 * @类描述：
 * @author 江荣波 2017年6月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLoanDto extends AfLoanDo {

	private static final long serialVersionUID = -3162832676757115210L;
	
	//实时推送的白领贷债权相关信息
	private String orderNo;//借款订单号
	private Long userId;//借款人Id
	private String name;//借款人姓名
	private String cardId;//借款人身份证号
	private String mobile;//借款人手机号码
	private String bankNo;//银行卡号
	private String cardName;//银行卡所属行
	private BigDecimal money;//用户实际到账金额
	private Date loanStartTime;//借款开始时间戳（单位：秒）
    private String loanRemark;//借款用途
    private String repayRemark;//还款来源
 
	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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
	 * @return the cardId
	 */
	public String getCardId() {
		return cardId;
	}
	/**
	 * @param cardId the cardId to set
	 */
	public void setCardId(String cardId) {
		this.cardId = cardId;
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
	 * @return the bankNo
	 */
	public String getBankNo() {
		return bankNo;
	}
	/**
	 * @param bankNo the bankNo to set
	 */
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
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
	 * @return the money
	 */
	public BigDecimal getMoney() {
		return money;
	}
	/**
	 * @param money the money to set
	 */
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	/**
	 * @return the loanStartTime
	 */
	public Date getLoanStartTime() {
		return loanStartTime;
	}
	/**
	 * @param loanStartTime the loanStartTime to set
	 */
	public void setLoanStartTime(Date loanStartTime) {
		this.loanStartTime = loanStartTime;
	}
	/**
	 * @return the loanRemark
	 */
	public String getLoanRemark() {
		return loanRemark;
	}
	/**
	 * @param loanRemark the loanRemark to set
	 */
	public void setLoanRemark(String loanRemark) {
		this.loanRemark = loanRemark;
	}
	/**
	 * @return the repayRemark
	 */
	public String getRepayRemark() {
		return repayRemark;
	}
	/**
	 * @param repayRemark the repayRemark to set
	 */
	public void setRepayRemark(String repayRemark) {
		this.repayRemark = repayRemark;
	}
}
