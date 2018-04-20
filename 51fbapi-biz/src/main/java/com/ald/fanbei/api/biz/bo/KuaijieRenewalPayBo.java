package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;

public class KuaijieRenewalPayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieRenewalPayBo() {

    }

    public KuaijieRenewalPayBo(AfRenewalDetailDo renewalDetail) {
	this.renewalDetail = renewalDetail;
    }

    private AfRenewalDetailDo renewalDetail;

    public AfRenewalDetailDo getRenewalDetail() {
	return renewalDetail;
    }

    public void setRenewalDetail(AfRenewalDetailDo renewalDetail) {
	this.renewalDetail = renewalDetail;
    }

    @Override
    public String toString() {
	return "KuaijieRenewalPayBo [renewalDetail=" + renewalDetail + "]";
    }

}
