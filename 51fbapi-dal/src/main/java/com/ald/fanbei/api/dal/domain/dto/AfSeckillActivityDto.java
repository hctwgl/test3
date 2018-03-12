package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;

public class AfSeckillActivityDto extends AfSeckillActivityDo {
    private Integer limitCount;
    private Integer goodsLimitCount;
    private String payType;
    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Integer getGoodsLimitCount() {
        return goodsLimitCount;
    }

    public void setGoodsLimitCount(Integer goodsLimitCount) {
        this.goodsLimitCount = goodsLimitCount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
