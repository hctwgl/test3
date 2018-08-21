/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户现金借款逾期信息统计信息
 * @author wujun
 */
public class DsedOverdueInfoDto implements Serializable {

	private static final long serialVersionUID = 1995997550363931486L;
	
	private Integer overdueNums;
	private BigDecimal overdueAmount;
	
	public DsedOverdueInfoDto() {
		super();
	}
	public DsedOverdueInfoDto(Integer overdueNums,
			BigDecimal overdueAmount) {
		super();
		this.overdueNums = overdueNums;
		this.overdueAmount = overdueAmount;
	}
	public Integer getOverdueNums() {
		return overdueNums;
	}
	public void setOverdueNums(Integer overdueNums) {
		this.overdueNums = overdueNums;
	}
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

}
