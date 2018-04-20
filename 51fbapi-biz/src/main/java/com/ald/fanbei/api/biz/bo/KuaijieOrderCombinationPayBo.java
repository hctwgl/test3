package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

public class KuaijieOrderCombinationPayBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public KuaijieOrderCombinationPayBo() {

    }

    public KuaijieOrderCombinationPayBo(AfOrderDo orderInfo, AfBorrowDo borrow, AfUserAccountDo userAccountInfo, Map<String, Object> virtualMap) {
	this.orderInfo = orderInfo;
	this.borrow = borrow;
	this.userAccountInfo = userAccountInfo;
	this.virtualMap = virtualMap;
    }

    private AfOrderDo orderInfo;
    private AfBorrowDo borrow;
    private AfUserAccountDo userAccountInfo;
    private Map<String, Object> virtualMap;

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

    public AfUserAccountDo getUserAccountInfo() {
	return userAccountInfo;
    }

    public void setUserAccountInfo(AfUserAccountDo userAccountInfo) {
	this.userAccountInfo = userAccountInfo;
    }

    public Map<String, Object> getVirtualMap() {
	return virtualMap;
    }

    public void setVirtualMap(Map<String, Object> virtualMap) {
	this.virtualMap = virtualMap;
    }

    @Override
    public String toString() {
	return "KuaijieOrderCombinationPayBo [orderInfo=" + orderInfo + ", borrow=" + borrow + ", userAccountInfo=" + userAccountInfo + ", virtualMap=" + virtualMap + "]";
    }

}
