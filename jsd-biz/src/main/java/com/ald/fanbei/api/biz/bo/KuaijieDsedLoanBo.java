package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;

import java.io.Serializable;

public class KuaijieDsedLoanBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieDsedLoanBo() {

    }

    public KuaijieDsedLoanBo(DsedLoanRepaymentDo repayment, LoanRepayBo bo) {
	this.repayment = repayment;
	this.bo = bo;
    }

    private DsedLoanRepaymentDo repayment;
    private LoanRepayBo bo;

    public DsedLoanRepaymentDo getRepayment() {
	return repayment;
    }

    public void setRepayment(DsedLoanRepaymentDo repayment) {
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
