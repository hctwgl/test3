package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author fumeiai 2017年5月18日 15:52:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRenewalDetailDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long borrowId;// 借款ID
	private String status;// 状态【APPLY:续期申请中，SUCCESS:续期成功 , FAIL:续期失败】
	private Date gmtPlanRepayment;// 原预计还款时间
	private BigDecimal renewalAmount;// 续期本金
	private BigDecimal priorInterest;// 上期利息
	private BigDecimal priorOverdue;// 上期滞纳金
	private BigDecimal nextPoundage;// 下期手续费
	private BigDecimal jfbAmount;// 集分宝个数
	private BigDecimal rebateAmount;// 账户余额
	private BigDecimal actualAmount;// 实付金额
	private String cardName;// 支付方式（卡名称）
	private String cardNumber;// 卡号
	private String payTradeNo;// 平台提供给三方支付的交易流水号
	private String tradeNo;// 第三方的交易流水号
	private int renewalDay;// 续期天数
	private Long userId;// 用户编号
	private BigDecimal poundageRate;// 借钱手续费率（日）
	private BigDecimal baseBankRate;// 央行基准利率

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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

	public Long getBorrowId() {
		return borrowId;
	}

	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getGmtPlanRepayment() {
		return gmtPlanRepayment;
	}

	public void setGmtPlanRepayment(Date gmtPlanRepayment) {
		this.gmtPlanRepayment = gmtPlanRepayment;
	}

	public BigDecimal getRenewalAmount() {
		return renewalAmount;
	}

	public void setRenewalAmount(BigDecimal renewalAmount) {
		this.renewalAmount = renewalAmount;
	}

	public BigDecimal getPriorOverdue() {
		return priorOverdue;
	}

	public void setPriorOverdue(BigDecimal priorOverdue) {
		this.priorOverdue = priorOverdue;
	}

	public BigDecimal getNextPoundage() {
		return nextPoundage;
	}

	public void setNextPoundage(BigDecimal nextPoundage) {
		this.nextPoundage = nextPoundage;
	}

	public BigDecimal getJfbAmount() {
		return jfbAmount;
	}

	public void setJfbAmount(BigDecimal jfbAmount) {
		this.jfbAmount = jfbAmount;
	}

	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getPayTradeNo() {
		return payTradeNo;
	}

	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getPriorInterest() {
		return priorInterest;
	}

	public void setPriorInterest(BigDecimal priorInterest) {
		this.priorInterest = priorInterest;
	}

	public BigDecimal getPoundageRate() {
		return poundageRate;
	}

	public void setPoundageRate(BigDecimal poundageRate) {
		this.poundageRate = poundageRate;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public BigDecimal getBaseBankRate() {
		return baseBankRate;
	}

	public void setBaseBankRate(BigDecimal baseBankRate) {
		this.baseBankRate = baseBankRate;
	}

	public int getRenewalDay() {
		return renewalDay;
	}

	public void setRenewalDay(int renewalDay) {
		this.renewalDay = renewalDay;
	}

	@Override
	public String toString() {
		return "AfRenewalDetailDo [rid=" + rid + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", borrowId=" + borrowId + ", status=" + status + ", gmtPlanRepayment=" + gmtPlanRepayment + ", renewalAmount=" + renewalAmount + ", priorInterest=" + priorInterest + ", priorOverdue=" + priorOverdue + ", nextPoundage=" + nextPoundage + ", jfbAmount=" + jfbAmount + ", rebateAmount=" + rebateAmount + ", actualAmount=" + actualAmount + ", cardName=" + cardName + ", cardNumber=" + cardNumber + ", payTradeNo=" + payTradeNo + ", tradeNo=" + tradeNo + ", renewalDay=" + renewalDay + ", userId=" + userId + ", poundageRate=" + poundageRate + ", baseBankRate=" + baseBankRate + "]";
	}

}
