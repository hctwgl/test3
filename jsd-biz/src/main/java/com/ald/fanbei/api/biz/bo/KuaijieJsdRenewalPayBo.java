package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;

import java.io.Serializable;

public class KuaijieJsdRenewalPayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieJsdRenewalPayBo() {

    }

    public KuaijieJsdRenewalPayBo(JsdBorrowCashRenewalDo repayment, JsdRenewalDealBo bo) {
	this.renewalDo = repayment;
	this.bo = bo;
    }

    private JsdBorrowCashRenewalDo renewalDo;
    private JsdRenewalDealBo bo;

    public JsdBorrowCashRenewalDo getRenewal() {
	return renewalDo;
    }

    public void setRenewal(JsdBorrowCashRenewalDo renewalDo) {
	this.renewalDo = renewalDo;
    }

    public JsdRenewalDealBo getBo() {
	return bo;
    }

    public void setBo(JsdRenewalDealBo bo) {
	this.bo = bo;
    }

    @Override
    public String toString() {
	return "KuaijieRenewalBo [renewalDo=" + renewalDo + ", bo=" + bo + "]";
    }

}
