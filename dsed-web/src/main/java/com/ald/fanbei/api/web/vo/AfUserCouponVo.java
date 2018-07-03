package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 *@类描述：AfUserCouponVo
 *@author 何鑫 2017年1月20日  14:49:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfUserCouponVo extends AbstractSerial{
	
	private static final long serialVersionUID = 3223281017295487747L;
	
	private Long		rid;//优惠券id

	private String 		name;//优惠券名字
	
	private BigDecimal	amount;//优惠金额
	
	private BigDecimal	limitAmount;//限制启用金额
	
	private String		useRule;//使用须知
	
	private Date		gmtStart;//开始时间
	
	private Date		gmtEnd;//截止时间
	
	private String		status;//优惠券状态 EXPIRE:过期 ; NOUSE:未使用 ， USED:已使用
	private String		type;
	
	private String      willExpireStatus;
	
	private String shopUrl;
	
	private String useRange;

	private Integer isGlobal;

	private BigDecimal discount;

	private String activityType;

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

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getUseRule() {
		return useRule;
	}

	public void setUseRule(String useRule) {
		this.useRule = useRule;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getWillExpireStatus() {
		return willExpireStatus;
	}

	public void setWillExpireStatus(String isExpire) {
		this.willExpireStatus = isExpire;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public String getUseRange() {
		return useRange;
	}

	public void setUseRange(String useRange) {
		this.useRange = useRange;
	}
}