package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

public class KuaijieRepaymentBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRepaymentBo() {

    }

    public KuaijieRepaymentBo(Object repayment, Object bills) {
	this.repayment = repayment;
	this.bills = bills;
    }

    private Object repayment;
    private Object bills;

    public Object getRepayment() {
	return repayment;
    }

    public void setRepayment(Object repayment) {
	this.repayment = repayment;
    }

    public Object getBills() {
	return bills;
    }

    public void setBills(Object bills) {
	this.bills = bills;
    }

    @Override
    public String toString() {
	return "KuaijieRepaymentBo [repayment=" + repayment + ", bills=" + bills + "]";
    }

}
