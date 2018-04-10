package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

public class KuaijieRepayV2Bo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRepayV2Bo() {

    }

    public KuaijieRepayV2Bo(AfRepaymentBorrowCashDo repayment) {
	this.repayment = repayment;
    }

    private AfRepaymentBorrowCashDo repayment;

    public AfRepaymentBorrowCashDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfRepaymentBorrowCashDo repayment) {
	this.repayment = repayment;
    }

    @Override
    public String toString() {
	return "KuaijieRepayV2Bo [repayment=" + repayment + "]";
    }

}
