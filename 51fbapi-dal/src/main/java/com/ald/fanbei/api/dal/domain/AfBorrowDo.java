package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：借款表
 * @author hexin 2017年2月09日上午11:16:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowDo extends AbstractSerial{

	private static final long serialVersionUID = 3667681285630288268L;

	private Long rid;
	
	private Date gmtCreate;
	
	private Date gmtModified;
	
	private Long userId;//用户id
	
	private String type;//借款类型：【CASH:现金 ,CONSUME:消费,INSTALMENT:消费分期】
	
	private String name;//借款名称
	
	private BigDecimal amount;//借款金额
	
	private String status;//状态【APPLY:申请/未审核 , AGREE:同意/审核通过 , CANCEL:取消 , CLOSE:关闭】
	
	private String remark;//备注
	
	private Integer nper;//分期数
	
	private BigDecimal perAmount;//每期账单
	
	private Integer nperRepaid;//已还款期数
	
	private String interestType;//利息类型【F:free免息 , C:charges收费】
	
	private BigDecimal totalInterest;//总利息
	
	private BigDecimal totalPoundage;//总手续费
	
	private Integer	timeoutNum;//逾期期数
	
	private Long orderId;//订单id

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}

	public Integer getNperRepaid() {
		return nperRepaid;
	}

	public void setNperRepaid(Integer nperRepaid) {
		this.nperRepaid = nperRepaid;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getTotalPoundage() {
		return totalPoundage;
	}

	public void setTotalPoundage(BigDecimal totalPoundage) {
		this.totalPoundage = totalPoundage;
	}

	public Integer getTimeoutNum() {
		return timeoutNum;
	}

	public void setTimeoutNum(Integer timeoutNum) {
		this.timeoutNum = timeoutNum;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getPerAmount() {
		return perAmount;
	}

	public void setPerAmount(BigDecimal perAmount) {
		this.perAmount = perAmount;
	}
	
}
