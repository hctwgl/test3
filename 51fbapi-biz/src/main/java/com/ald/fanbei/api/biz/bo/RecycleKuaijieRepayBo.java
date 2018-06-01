package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.biz.service.impl.AfBorrowRecycleRepaymentServiceImpl.RepayBo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

public class RecycleKuaijieRepayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public RecycleKuaijieRepayBo() {

    }

    public RecycleKuaijieRepayBo(AfRepaymentBorrowCashDo repayment, RepayBo bo) {
	this.repayment = repayment;
	this.bo = bo;
    }

    private AfRepaymentBorrowCashDo repayment;
    private RepayBo bo;

    public AfRepaymentBorrowCashDo getRepayment() {
	return repayment;
    }

    public void setRepayment(AfRepaymentBorrowCashDo repayment) {
	this.repayment = repayment;
    }

    public RepayBo getBo() {
	return bo;
    }

    public void setBo(RepayBo bo) {
	this.bo = bo;
    }

    @Override
    public String toString() {
	return "RecycleKuaijieRepayBo [repayment=" + repayment + ", bo=" + bo + "]";
    }

}
