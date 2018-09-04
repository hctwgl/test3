package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.BorrowCashRepayBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;

public class KuaijieRepayBo {

    private static final long serialVersionUID = 1L;

    public KuaijieRepayBo() {


    }

    public KuaijieRepayBo(JsdBorrowCashRepaymentDo repayment, BorrowCashRepayBo bo) {
        this.repayment = repayment;
        this.bo = bo;
    }

    private JsdBorrowCashRepaymentDo repayment;
    private BorrowCashRepayBo bo;

    public JsdBorrowCashRepaymentDo getRepayment() {
        return repayment;
    }

    public void setRepayment(JsdBorrowCashRepaymentDo repayment) {
        this.repayment = repayment;
    }

    public BorrowCashRepayBo getBo() {
        return bo;
    }

    public void setBo(BorrowCashRepayBo bo) {
        this.bo = bo;
    }

    @Override
    public String toString() {
        return "KuaijieRepayV2Bo [repayment=" + repayment + ", bo=" + bo + "]";
    }


}
