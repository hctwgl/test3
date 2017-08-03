package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 
 * <p>Title:BoluomeCouponForGGVO <p>
 * <p>Description: <p>
 * @Copyright (c)  浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 * @author qiao
 * @date 2017年8月2日下午5:21:51
 *
 */
public class BoluomeCouponForGGVO extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1263843909844249762L;

	private Long rid;
	private String type;
	private String description;
	private BigDecimal amount;
	private Integer validDays;
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Integer getValidDays() {
		return validDays;
	}
	public void setValidDays(Integer validDays) {
		this.validDays = validDays;
	}
	
}
