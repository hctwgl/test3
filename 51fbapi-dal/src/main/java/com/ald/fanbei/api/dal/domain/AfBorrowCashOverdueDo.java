/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午5:27:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowCashOverdueDo extends AbstractSerial {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Long userId;
	private Long borrowCashId;
	private BigDecimal interest;
	private BigDecimal currentAmount;

	
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
	 * @return the borrowCashId
	 */
	public Long getBorrowCashId() {
		return borrowCashId;
	}
	/**
	 * @param borrowCashId the borrowCashId to set
	 */
	public void setBorrowCashId(Long borrowCashId) {
		this.borrowCashId = borrowCashId;
	}
	/**
	 * @return the interest
	 */
	public BigDecimal getInterest() {
		return interest;
	}
	/**
	 * @param interest the interest to set
	 */
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
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
	/**
	 * @return the currentAmount
	 */
	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}
	/**
	 * @param currentAmount the currentAmount to set
	 */
	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}
	
}
