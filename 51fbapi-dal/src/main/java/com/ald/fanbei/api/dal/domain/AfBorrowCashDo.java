package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月24日下午4:03:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowCashDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String borrowNo;
	private Long userId;
	private String type;
	private BigDecimal amount;
	private String status;
	private String remark;
	private String cardNumber;
	private String cardName;
	private Long overdueDay;
	private BigDecimal overdueAmount;
	private BigDecimal arrivalAmount;
	private Date gmtArrival;
	private String reviewName;
	private String reviewUserName;
	private String reviewDetails;
	private Date gmtReview;
	private Date gmtClose;

	private BigDecimal poundage;
	private BigDecimal rateAmount;
	private BigDecimal repayAmount;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String province;
	private String city;
	private String county;
	private String overdueStatus;
	private String reviewStatus;
	private String address;

	private String rishOrderNo;

	private Date gmtPlanRepayment;// 预计还款时间
	private BigDecimal sumRate = BigDecimal.ZERO; // 累计利息
	private BigDecimal sumOverdue = BigDecimal.ZERO; // 累计滞纳金
	private BigDecimal sumRenewalPoundage = BigDecimal.ZERO; // 累计续期手续费
	private BigDecimal sumRebate = BigDecimal.ZERO; // 累计使用账户余额
	private BigDecimal sumJfb = BigDecimal.ZERO; // 累计使用集分宝
	private int renewalNum; //累计续期次数
	/**
	 * @return the gmtClose
	 */
	public Date getGmtClose() {
		return gmtClose;
	}

	/**
	 * @param gmtClose
	 *            the gmtClose to set
	 */
	public void setGmtClose(Date gmtClose) {
		this.gmtClose = gmtClose;
	}

	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}

	/**
	 * @param gmtCreate
	 *            the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * @param gmtModified
	 *            the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * @return the borrowNo
	 */
	public String getBorrowNo() {
		return borrowNo;
	}

	/**
	 * @param borrowNo
	 *            the borrowNo to set
	 */
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber
	 *            the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the cardName
	 */
	public String getCardName() {
		return cardName;
	}

	/**
	 * @param cardName
	 *            the cardName to set
	 */
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	/**
	 * @return the overdueDay
	 */
	public Long getOverdueDay() {
		return overdueDay;
	}

	/**
	 * @param overdueDay
	 *            the overdueDay to set
	 */
	public void setOverdueDay(Long overdueDay) {
		this.overdueDay = overdueDay;
	}

	/**
	 * @return the overdueAmount
	 */
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	/**
	 * @param overdueAmount
	 *            the overdueAmount to set
	 */
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	/**
	 * @return the arrivalAmount
	 */
	public BigDecimal getArrivalAmount() {
		return arrivalAmount;
	}

	/**
	 * @param arrivalAmount
	 *            the arrivalAmount to set
	 */
	public void setArrivalAmount(BigDecimal arrivalAmount) {
		this.arrivalAmount = arrivalAmount;
	}

	/**
	 * @return the gmtArrival
	 */
	public Date getGmtArrival() {
		return gmtArrival;
	}

	/**
	 * @param gmtArrival
	 *            the gmtArrival to set
	 */
	public void setGmtArrival(Date gmtArrival) {
		this.gmtArrival = gmtArrival;
	}

	/**
	 * @return the reviewName
	 */
	public String getReviewName() {
		return reviewName;
	}

	/**
	 * @param reviewName
	 *            the reviewName to set
	 */
	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}

	/**
	 * @return the reviewUserName
	 */
	public String getReviewUserName() {
		return reviewUserName;
	}

	/**
	 * @param reviewUserName
	 *            the reviewUserName to set
	 */
	public void setReviewUserName(String reviewUserName) {
		this.reviewUserName = reviewUserName;
	}

	/**
	 * @return the reviewDetails
	 */
	public String getReviewDetails() {
		return reviewDetails;
	}

	/**
	 * @param reviewDetails
	 *            the reviewDetails to set
	 */
	public void setReviewDetails(String reviewDetails) {
		this.reviewDetails = reviewDetails;
	}

	/**
	 * @return the gmtReview
	 */
	public Date getGmtReview() {
		return gmtReview;
	}

	/**
	 * @param gmtReview
	 *            the gmtReview to set
	 */
	public void setGmtReview(Date gmtReview) {
		this.gmtReview = gmtReview;
	}

	/**
	 * @return the poundage
	 */
	public BigDecimal getPoundage() {
		return poundage;
	}

	/**
	 * @param poundage
	 *            the poundage to set
	 */
	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
	}

	/**
	 * @return the rateAmount
	 */
	public BigDecimal getRateAmount() {
		return rateAmount;
	}

	/**
	 * @param rateAmount
	 *            the rateAmount to set
	 */
	public void setRateAmount(BigDecimal rateAmount) {
		this.rateAmount = rateAmount;
	}

	/**
	 * @return the repayAmount
	 */
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	/**
	 * @param repayAmount
	 *            the repayAmount to set
	 */
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	/**
	 * @return the latitude
	 */
	public BigDecimal getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public BigDecimal getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}

	/**
	 * @param county
	 *            the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}

	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}

	/**
	 * @param rid
	 *            the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}

	/**
	 * @return the overdueStatus
	 */
	public String getOverdueStatus() {
		return overdueStatus;
	}

	/**
	 * @param overdueStatus
	 *            the overdueStatus to set
	 */
	public void setOverdueStatus(String overdueStatus) {
		this.overdueStatus = overdueStatus;
	}

	/**
	 * @return the reviewStatus
	 */
	public String getReviewStatus() {
		return reviewStatus;
	}

	/**
	 * @param reviewStatus
	 *            the reviewStatus to set
	 */
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	public String getRishOrderNo() {
		return rishOrderNo;
	}

	public void setRishOrderNo(String rishOrderNo) {
		this.rishOrderNo = rishOrderNo;
	}

	public Date getGmtPlanRepayment() {
		return gmtPlanRepayment;
	}

	public void setGmtPlanRepayment(Date gmtPlanRepayment) {
		this.gmtPlanRepayment = gmtPlanRepayment;
	}

	public BigDecimal getSumRate() {
		return sumRate;
	}

	public void setSumRate(BigDecimal sumRate) {
		this.sumRate = sumRate;
	}

	public BigDecimal getSumOverdue() {
		return sumOverdue;
	}

	public void setSumOverdue(BigDecimal sumOverdue) {
		this.sumOverdue = sumOverdue;
	}

	public BigDecimal getSumRenewalPoundage() {
		return sumRenewalPoundage;
	}

	public void setSumRenewalPoundage(BigDecimal sumRenewalPoundage) {
		this.sumRenewalPoundage = sumRenewalPoundage;
	}

	public BigDecimal getSumRebate() {
		return sumRebate;
	}

	public void setSumRebate(BigDecimal sumRebate) {
		this.sumRebate = sumRebate;
	}

	public BigDecimal getSumJfb() {
		return sumJfb;
	}

	public void setSumJfb(BigDecimal sumJfb) {
		this.sumJfb = sumJfb;
	}

	public int getRenewalNum() {
		return renewalNum;
	}

	public void setRenewalNum(int renewalNum) {
		this.renewalNum = renewalNum;
	}

	@Override
	public String toString() {
		return "AfBorrowCashDo [rid=" + rid + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", borrowNo=" + borrowNo + ", userId=" + userId + ", type=" + type + ", amount=" + amount + ", status=" + status + ", remark=" + remark + ", cardNumber=" + cardNumber + ", cardName=" + cardName + ", overdueDay=" + overdueDay + ", overdueAmount=" + overdueAmount + ", arrivalAmount=" + arrivalAmount + ", gmtArrival=" + gmtArrival + ", reviewName=" + reviewName + ", reviewUserName=" + reviewUserName + ", reviewDetails=" + reviewDetails + ", gmtReview=" + gmtReview + ", gmtClose=" + gmtClose + ", poundage=" + poundage + ", rateAmount=" + rateAmount + ", repayAmount=" + repayAmount + ", latitude=" + latitude + ", longitude=" + longitude + ", province=" + province + ", city=" + city + ", county=" + county + ", overdueStatus=" + overdueStatus + ", reviewStatus=" + reviewStatus + ", address=" + address + ", rishOrderNo=" + rishOrderNo + ", gmtPlanRepayment=" + gmtPlanRepayment
				+ ", sumRate=" + sumRate + ", sumOverdue=" + sumOverdue + ", sumRenewalPoundage=" + sumRenewalPoundage + ", sumRebate=" + sumRebate + ", sumJfb=" + sumJfb + ", renewalNum=" + renewalNum + "]";
	}

}
