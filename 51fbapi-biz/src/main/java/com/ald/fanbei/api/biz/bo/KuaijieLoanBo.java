package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;

public class KuaijieLoanBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieLoanBo() {

    }

    public KuaijieLoanBo(AfLoanRepaymentDo repayment) {
	this.repayment = repayment;
    }

    private AfLoanRepaymentDo repayment;

    public AfLoanRepaymentDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfLoanRepaymentDo repayment) {
	this.repayment = repayment;
    }

    @Override
    public String toString() {
	return "KuaijieLoanBo [repayment=" + repayment + "]";
    }

}
