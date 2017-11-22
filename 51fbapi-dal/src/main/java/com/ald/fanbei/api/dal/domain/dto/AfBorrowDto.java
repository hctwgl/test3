package com.ald.fanbei.api.dal.domain.dto;

import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;

public class AfBorrowDto extends AfBorrowDo {

	private static final long serialVersionUID = 7506645755979915414L;
	
	private Date payDate;

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	
	
}
