package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @类现描述：钱包平台获取债权对应的用户信息实体
 * @author chengkang 2017年12月04日 11:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetPlatUserInfoRespBo implements Serializable {

	private static final long serialVersionUID = 6309413246622250673L;

	private Long userId;// 借款人Id
	private String orderNo;// 借款订单号
	private Integer loanerType;// 借款人性质
	private Integer overdueTimes;// 平台逾期次数
	private BigDecimal overdueAmount;// 平台逾期金额
	private BigDecimal repayAmount;// 还款金额
	private Integer repaymentStatus;// 还款状态（0：未还，1：已还）
	private Long repaymentTime;// 还款时间(还款状态为1时必传)
	private Integer isOverdue;// 是否逾期(0:否[Default],1:是)

	public EdspayGetPlatUserInfoRespBo() {
		super();
	}

	public EdspayGetPlatUserInfoRespBo(Long userId, String orderNo,
			Integer loanerType, Integer overdueTimes, BigDecimal overdueAmount,
			BigDecimal repayAmount, Integer repaymentStatus,
			Long repaymentTime, Integer isOverdue) {
		super();
		this.userId = userId;
		this.orderNo = orderNo;
		this.loanerType = loanerType;
		this.overdueTimes = overdueTimes;
		this.overdueAmount = overdueAmount;
		this.repayAmount = repayAmount;
		this.repaymentStatus = repaymentStatus;
		this.repaymentTime = repaymentTime;
		this.isOverdue = isOverdue;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getLoanerType() {
		return loanerType;
	}

	public void setLoanerType(Integer loanerType) {
		this.loanerType = loanerType;
	}

	public Integer getOverdueTimes() {
		return overdueTimes;
	}

	public void setOverdueTimes(Integer overdueTimes) {
		this.overdueTimes = overdueTimes;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}

	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	public Long getRepaymentTime() {
		return repaymentTime;
	}

	public void setRepaymentTime(Long repaymentTime) {
		this.repaymentTime = repaymentTime;
	}

	public Integer getIsOverdue() {
		return isOverdue;
	}

	public void setIsOverdue(Integer isOverdue) {
		this.isOverdue = isOverdue;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
