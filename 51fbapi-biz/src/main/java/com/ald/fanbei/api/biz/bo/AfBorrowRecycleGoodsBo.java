package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.List;

public class AfBorrowRecycleGoodsBo extends AbstractSerial {
    private String name;

    private String goodsImg;

    private List<Object> propertyValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public List<Object> getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(List<Object> propertyValue) {
        this.propertyValue = propertyValue;
    }
}
