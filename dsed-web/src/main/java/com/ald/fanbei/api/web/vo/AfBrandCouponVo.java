package com.ald.fanbei.api.web.vo;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日上午11:14:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBrandCouponVo extends AbstractSerial {

	private static final long serialVersionUID = -1278526599458583341L;
	
	private String name;//优惠券名称
	private Float amount;//使用优惠券抵用的现金价值或者是折扣
	private Float maxAmount;//当为折扣时可抵用的最大现金价值
	private Float limitAmount;//优惠券使用阈值, 期含义跟type 类型有关 type = 1,2是表示满足的金额, type = 3.4表示满足的数量
	private Date gmtStart;//开始时间
	private Date gmtEnd;//截止时间
	private Integer type;//优惠券类型. 1金额满减 2.金额满折 3.数量满减 4.数量满折
	private String status;//状态：EXPIRE:过期 ; NOUSE:未使用 ， USED:已使用
	private String willExpireStatus;
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
	 * @return the amount
	 */
	public Float getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	/**
	 * @return the maxAmount
	 */
	public Float getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(Float maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return the limitAmount
	 */
	public Float getLimitAmount() {
		return limitAmount;
	}
	/**
	 * @param limitAmount the limitAmount to set
	 */
	public void setLimitAmount(Float limitAmount) {
		this.limitAmount = limitAmount;
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
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWillExpireStatus() {
		return willExpireStatus;
	}
	public void setWillExpireStatus(String isExpire) {
		this.willExpireStatus = isExpire;
	}
}
