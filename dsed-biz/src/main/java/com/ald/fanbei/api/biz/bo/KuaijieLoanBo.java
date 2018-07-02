package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.biz.service.impl.AfLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;

public class KuaijieLoanBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieLoanBo() {

    }

    public KuaijieLoanBo(AfLoanRepaymentDo repayment, LoanRepayBo bo) {
	this.repayment = repayment;
	this.bo = bo;
    }

    private AfLoanRepaymentDo repayment;
    private LoanRepayBo bo;

    public AfLoanRepaymentDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfLoanRepaymentDo repayment) {
	this.repayment = repayment;
    }

    public LoanRepayBo getBo() {
	return bo;
    }

    public void setBo(LoanRepayBo bo) {
	this.bo = bo;
    }

    @Override
    public String toString() {
	return "KuaijieLoanBo [repayment=" + repayment + ", bo=" + bo + "]";
    }

}
