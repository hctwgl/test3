package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderLeaseDo;

public class KuaijieOrderPayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieOrderPayBo() {

    }

    public KuaijieOrderPayBo(AfOrderDo orderInfo, AfBorrowDo borrow, AfOrderLeaseDo afOrderLeaseDo) {
	this.orderInfo = orderInfo;
	this.borrow = borrow;
	this.afOrderLeaseDo = afOrderLeaseDo;
    }

    private AfOrderDo orderInfo;
    private AfBorrowDo borrow;
    private AfOrderLeaseDo afOrderLeaseDo;

    public AfOrderDo getOrderInfo() {
	return orderInfo;
    }

    public void setOrderInfo(AfOrderDo orderInfo) {
	this.orderInfo = orderInfo;
    }

    public AfBorrowDo getBorrow() {
	return borrow;
    }

    public void setBorrow(AfBorrowDo borrow) {
	this.borrow = borrow;
    }

    public AfOrderLeaseDo getAfOrderLeaseDo() {
	return afOrderLeaseDo;
    }

    public void setAfOrderLeaseDo(AfOrderLeaseDo afOrderLeaseDo) {
	this.afOrderLeaseDo = afOrderLeaseDo;
    }

    @Override
    public String toString() {
	return "KuaijieOrderPayBo [orderInfo=" + orderInfo + ", borrow=" + borrow + ", afOrderLeaseDo=" + afOrderLeaseDo + "]";
    }

}
