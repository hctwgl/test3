package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.biz.service.impl.AfBorrowLegalRepaymentServiceImpl.RepayBo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

public class KuaijieRepayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRepayBo() {

    }

    public KuaijieRepayBo(AfRepaymentBorrowCashDo repayment, AfBorrowLegalOrderRepaymentDo legalOrderRepayment, RepayBo bo) {
	this.repayment = repayment;
	this.legalOrderRepayment = legalOrderRepayment;
	this.bo = bo;
    }

    private AfRepaymentBorrowCashDo repayment;
    private AfBorrowLegalOrderRepaymentDo legalOrderRepayment;
    private RepayBo bo;

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

    public RepayBo getBo() {
	return bo;
    }

    public void setBo(RepayBo bo) {
	this.bo = bo;
    }

    @Override
    public String toString() {
	return "KuaijieRepayBo [repayment=" + repayment + ", legalOrderRepayment=" + legalOrderRepayment + ", bo=" + bo + "]";
    }

}
