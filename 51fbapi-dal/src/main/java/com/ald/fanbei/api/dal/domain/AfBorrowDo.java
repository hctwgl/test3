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
	
	private String borrowNo;//借款编号
	
	private String type;//借款类型：【CASH:现金 ,CONSUME:消费,INSTALMENT:消费分期】
	
	private String name;//借款名称
	
	private BigDecimal amount;//借款金额
	
	private String status;//状态【APPLY:申请/未审核 , AGREE:同意/审核通过 , CANCEL:取消 , CLOSE:关闭】
	
	private String remark;//备注
	
	private Integer nper;//分期数
	
	private Integer nperRepayment;//已还款期数
	
	private Integer	overdueNum;//逾期期数
	
	private Long orderId;//订单id

	private String orderNo;
	
	private BigDecimal nperAmount;//每期应还金额
	
	private BigDecimal repayPrinAmount;//已还本金
	
	private String cardNumber;//卡编号
	
	private String cardName;//卡名称

	private Date gmtTransed;//打款时间

	private String borrowRate;//提交订单时的费率信息
	
	private String calculateMethod;//计息方式【DEBX:等额本息 DBDX:等本等息】
	
	private Integer freeNper;//免息期数;

	private Integer version;//1：V2版本 0：老版本

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

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

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
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

	public Integer getNperRepayment() {
		return nperRepayment;
	}

	public void setNperRepayment(Integer nperRepayment) {
		this.nperRepayment = nperRepayment;
	}

	public Integer getOverdueNum() {
		return overdueNum;
	}

	public void setOverdueNum(Integer overdueNum) {
		this.overdueNum = overdueNum;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getNperAmount() {
		return nperAmount;
	}

	public void setNperAmount(BigDecimal nperAmount) {
		this.nperAmount = nperAmount;
	}

	public BigDecimal getRepayPrinAmount() {
		return repayPrinAmount;
	}

	public void setRepayPrinAmount(BigDecimal repayPrinAmount) {
		this.repayPrinAmount = repayPrinAmount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the gmtTransed
	 */
	public Date getGmtTransed() {
		return gmtTransed;
	}

	/**
	 * @param gmtTransed the gmtTransed to set
	 */
	public void setGmtTransed(Date gmtTransed) {
		this.gmtTransed = gmtTransed;
	}

	/**
	 * @return the borrowRate
	 */
	public String getBorrowRate() {
		return borrowRate;
	}

	/**
	 * @param borrowRate the borrowRate to set
	 */
	public void setBorrowRate(String borrowRate) {
		this.borrowRate = borrowRate;
	}

	/**
	 * @return the calculateMethod
	 */
	public String getCalculateMethod() {
		return calculateMethod;
	}

	/**
	 * @param calculateMethod the calculateMethod to set
	 */
	public void setCalculateMethod(String calculateMethod) {
		this.calculateMethod = calculateMethod;
	}

	/**
	 * @return the freeNper
	 */
	public Integer getFreeNper() {
		return freeNper;
	}

	/**
	 * @param freeNper the freeNper to set
	 */
	public void setFreeNper(Integer freeNper) {
		this.freeNper = freeNper;
	}

	
}
