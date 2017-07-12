package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;

public class AfBorrowBillQuery extends Page<AfBorrowBillDo>{

	private static final long serialVersionUID = 5059432397386863022L;

	private int billYear;
	
	private int billMonth;
	
	private Long userId;

	public int getBillYear() {
		return billYear;
	}

	public void setBillYear(int billYear) {
		this.billYear = billYear;
	}

	public int getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(int billMonth) {
		this.billMonth = billMonth;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
