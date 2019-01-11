package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;

public class InHandTaskDto {
    //交易单号
    private String orderNos;
    //商户号
    private String merNo;


    public String getMerNo() {
        return ConfigProperties.get(Constants.CONFKEY_UPS_MERNO);
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    public String getOrderNos() {
        return orderNos;
    }

    public void setOrderNos(String orderNos) {
        this.orderNos = orderNos;
    }
}
