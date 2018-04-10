package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

public class KuaijieRepayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRepayBo() {

    }

    public KuaijieRepayBo(AfRepaymentBorrowCashDo repayment, AfBorrowLegalOrderRepaymentDo legalOrderRepayment) {
	this.repayment = repayment;
	this.legalOrderRepayment = legalOrderRepayment;
    }

    private AfRepaymentBorrowCashDo repayment;
    private AfBorrowLegalOrderRepaymentDo legalOrderRepayment;

    public AfRepaymentBorrowCashDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfRepaymentBorrowCashDo repayment) {
	this.repayment = repayment;
    }

    public AfBorrowLegalOrderRepaymentDo getLegalOrderRepayment() {
	return legalOrderRepayment;
    }

    public void setLegalOrderRepayment(AfBorrowLegalOrderRepaymentDo legalOrderRepayment) {
	this.legalOrderRepayment = legalOrderRepayment;
    }

    @Override
    public String toString() {
	return "KuaijieRepayBo [repayment=" + repayment + ", legalOrderRepayment=" + legalOrderRepayment + "]";
    }

}
