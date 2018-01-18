package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;

public class AfUserAmountQuery extends Page<AfUserAmountDo>{

	private static final long serialVersionUID = -6144241094884204078L;
	
	private Date strDate;
	
	private Date endDate;
	
	private Long userId;
	
	private Integer bizType;
	
	private String year;
	
	public Date getStrDate() {
		return strDate;
	}

	public void setStrDate(Date strDate) {
		this.strDate = strDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getBizType() {
		return bizType;
	}

	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
