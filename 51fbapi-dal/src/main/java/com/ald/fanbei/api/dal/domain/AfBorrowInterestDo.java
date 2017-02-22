package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：借款利息表表
 * @author hexin 2017年2月21日上午9:37:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowInterestDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811156491054166412L;
	private Long rid;
	private Date gmtCreate;
	private String creator;
	private Long borrowId;
	private BigDecimal interest;
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Long getBorrowId() {
		return borrowId;
	}
	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}
	public BigDecimal getInterest() {
		return interest;
	}
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
}
