package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayRequestBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;

public class ProtocolRepayBo {

    public ProtocolRepayBo() {


    }

    public ProtocolRepayBo(JsdBorrowCashRepaymentDo repayment, RepayRequestBo bo) {
        this.repayment = repayment;
        this.bo = bo;
    }

    private JsdBorrowCashRepaymentDo repayment;
    private RepayRequestBo bo;

    public JsdBorrowCashRepaymentDo getRepayment() {
        return repayment;
    }

    public void setRepayment(JsdBorrowCashRepaymentDo repayment) {
        this.repayment = repayment;
    }

    public RepayRequestBo getBo() {
        return bo;
    }

    public void setBo(RepayRequestBo bo) {
        this.bo = bo;
    }

    @Override
    public String toString() {
        return "KuaijieRepayV2Bo [repayment=" + repayment + ", bo=" + bo + "]";
    }


}
