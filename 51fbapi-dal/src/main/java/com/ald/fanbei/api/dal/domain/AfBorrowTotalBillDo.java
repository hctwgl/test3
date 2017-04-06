package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：AfBorrowTotalBillDo
 * @author hexin 2017年2月21日下午17:49:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowTotalBillDo extends AbstractSerial{

	private static final long serialVersionUID = 3555897652064168099L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Integer billYear;
	private Integer billMonth;
	private BigDecimal billAmount;
	private String status;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getBillYear() {
		return billYear;
	}
	public void setBillYear(Integer billYear) {
		this.billYear = billYear;
	}
	public Integer getBillMonth() {
		return billMonth;
	}
	public void setBillMonth(Integer billMonth) {
		this.billMonth = billMonth;
	}
	public BigDecimal getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
