package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.dal.domain.AfLoanDo;

public class AfLoanVo extends AfLoanDo {

	private Long loanPeriodsId;
	private int nper;
	
	private Long getLoanPeriodsId() {
		return loanPeriodsId;
	}
	private void setLoanPeriodsId(Long loanPeriodsId) {
		this.loanPeriodsId = loanPeriodsId;
	}
	private int getNper() {
		return nper;
	}
	private void setNper(int nper) {
		this.nper = nper;
	}
	
}
