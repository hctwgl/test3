/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午3:48:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCouponDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;//优惠券名字
	private BigDecimal limitAmount;//最小限制金额
	private BigDecimal amount;//优惠金额
	private String useRule;//使用须知
	private Integer totalCount;//总发放数量
	private Date gmtStart;//有效期开始时间
	private Date gmtEnd;//有效期结束时间
	private Integer validDays;//有效天数，与有效开始/结束时间互斥，要么设置有效开始时间有效结束时间，要么设置有效期  【  -1:表示永久有效;  0：表示设置了有效期;  >0:表示有效期（天数）】
	private Integer limitCount;
	private String status;	
	private String type; //优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励】
	private String expiryType;//有效期类型【D:days固定天数，R:range固定时间范围】
	private Integer quotaAlready;
	private Long quota;
	private String useRange;
	
	
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @return the limitCount
	 */
	public Integer getLimitCount() {
		return limitCount;
	}
	/**
	 * @param limitCount the limitCount to set
	 */
	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
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
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
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
	 * @return the limitAmount
	 */
	public BigDecimal getLimitAmount() {
		return limitAmount;
	}
	/**
	 * @param limitAmount the limitAmount to set
	 */
	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the useRule
	 */
	public String getUseRule() {
		return useRule;
	}
	/**
	 * @param useRule the useRule to set
	 */
	public void setUseRule(String useRule) {
		this.useRule = useRule;
	}
	/**
	 * @return the totalCount
	 */
	public Integer getTotalCount() {
		return totalCount;
	}
	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	
	/**
	 * @return the gmtStart
	 */
	public Date getGmtStart() {
		return gmtStart;
	}
	/**
	 * @param gmtStart the gmtStart to set
	 */
	public void setGmtStart(Date gmtStart) {
		this.gmtStart = gmtStart;
	}
	/**
	 * @return the gmtEnd
	 */
	public Date getGmtEnd() {
		return gmtEnd;
	}
	/**
	 * @param gmtEnd the gmtEnd to set
	 */
	public void setGmtEnd(Date gmtEnd) {
		this.gmtEnd = gmtEnd;
	}
	/**
	 * @return the validDays
	 */
	public Integer getValidDays() {
		return validDays;
	}
	/**
	 * @param validDays the validDays to set
	 */
	public void setValidDays(Integer validDays) {
		this.validDays = validDays;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the expiryType
	 */
	public String getExpiryType() {
		return expiryType;
	}
	/**
	 * @param expiryType the expiryType to set
	 */
	public void setExpiryType(String expiryType) {
		this.expiryType = expiryType;
	}
	/**
	 * @return the quotaAlready
	 */
	public Integer getQuotaAlready() {
		return quotaAlready;
	}
	/**
	 * @param quotaAlready the quotaAlready to set
	 */
	public void setQuotaAlready(Integer quotaAlready) {
		this.quotaAlready = quotaAlready;
	}
	/**
	 * @return the quota
	 */
	public Long getQuota() {
		return quota;
	}
	/**
	 * @param quota the quota to set
	 */
	public void setQuota(Long quota) {
		this.quota = quota;
	}
	
	public String getUseRange() {
		return useRange;
	}
	
	public void setUseRange(String useRange) {
		this.useRange = useRange;
	}
}