package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

public class KuaijieRepaymentBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRepaymentBo() {

    }

    public KuaijieRepaymentBo(AfRepaymentDo repayment, List<Long> bills) {
	this.repayment = repayment;
	this.bills = bills;
    }

    private AfRepaymentDo repayment;
    private List<Long> bills;

    public AfRepaymentDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfRepaymentDo repayment) {
	this.repayment = repayment;
    }

    public List<Long> getBills() {
	return bills;
    }

    public void setBills(List<Long> bills) {
	this.bills = bills;
    }

    @Override
    public String toString() {
	return "KuaijieRepaymentBo [repayment=" + repayment + ", bills=" + bills + "]";
    }
}
