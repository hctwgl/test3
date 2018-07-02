/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午3:48:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
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
	private String activityType;
	private Long activityId;
	private Integer isGlobal;
	private String creator;
	private String modifier;
	private String shopUrl;
	private String goodsIds;


	public AfCouponDo(){

	}

	public AfCouponDo(Long rid, Integer quotaAlready){
		this.rid = rid;
		this.quotaAlready = quotaAlready;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getUseRule() {
		return useRule;
	}

	public void setUseRule(String useRule) {
		this.useRule = useRule;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Date getGmtStart() {
		return gmtStart;
	}

	public void setGmtStart(Date gmtStart) {
		this.gmtStart = gmtStart;
	}

	public Date getGmtEnd() {
		return gmtEnd;
	}

	public void setGmtEnd(Date gmtEnd) {
		this.gmtEnd = gmtEnd;
	}

	public Integer getValidDays() {
		return validDays;
	}

	public void setValidDays(Integer validDays) {
		this.validDays = validDays;
	}

	public Integer getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
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

	public String getExpiryType() {
		return expiryType;
	}

	public void setExpiryType(String expiryType) {
		this.expiryType = expiryType;
	}

	public Integer getQuotaAlready() {
		return quotaAlready;
	}

	public void setQuotaAlready(Integer quotaAlready) {
		this.quotaAlready = quotaAlready;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public String getUseRange() {
		return useRange;
	}

	public void setUseRange(String useRange) {
		this.useRange = useRange;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Integer getIsGlobal() {
		return isGlobal;
	}

	public void setIsGlobal(Integer isGlobal) {
		this.isGlobal = isGlobal;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
}