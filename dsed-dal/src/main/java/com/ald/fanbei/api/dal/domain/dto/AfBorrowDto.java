package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;

public class AfBorrowDto extends AfBorrowDo {

	private static final long serialVersionUID = 7506645755979915414L;
	
	private Date payDate;
	
	private BigDecimal priceAmount;
	
	private BigDecimal bankAmount;
	
	private BigDecimal saleAmount;
	
	//实时推送给钱包的借款查询字段
	
	 /**
     * 消费分期债权id
     */
    private Long borrowId;

    /**
     * 消费分期债权第一期账单id
     */
    private Long minBorrowBillId;

    /**
     * 消费分期债权第一期账单创建时间
     */
    private Date gmtCreate;

    /**
     * 消费分期债权编号
     */
    private String borrowNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 分期金额， 每期应还金额
     */
    private BigDecimal nperAmount;

    /**
     * 分期数
     */
    private Integer nper;
	

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(BigDecimal bankAmount) {
		this.bankAmount = bankAmount;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	public Long getBorrowId() {
		return borrowId;
	}

	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}

	public Long getMinBorrowBillId() {
		return minBorrowBillId;
	}

	public void setMinBorrowBillId(Long minBorrowBillId) {
		this.minBorrowBillId = minBorrowBillId;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getNperAmount() {
		return nperAmount;
	}

	public void setNperAmount(BigDecimal nperAmount) {
		this.nperAmount = nperAmount;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}
	
}
