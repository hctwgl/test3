/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.sql.Date;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;

/**
 * 
 * @类描述：
 * @author 江荣波 2017年6月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfActivityGoodsDto extends AfGoodsDo {

	private static final long serialVersionUID = -3162832676757115210L;
	private Long id;
	private BigDecimal specialPrice;
	private Date startTime;
	private Date validStart;
	private Date validEnd;
	private Long goodsCount;
	private Long limitCount;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getSpecialPrice() {
		return specialPrice;
	}
	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getValidStart() {
		return validStart;
	}
	public void setValidStart(Date validStart) {
		this.validStart = validStart;
	}
	public Date getValidEnd() {
		return validEnd;
	}
	public void setValidEnd(Date validEnd) {
		this.validEnd = validEnd;
	}
	public Long getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(Long goodsCount) {
		this.goodsCount = goodsCount;
	}
	public Long getLimitCount() {
		return limitCount;
	}
	public void setLimitCount(Long limitCount) {
		this.limitCount = limitCount;
	}
	
}
