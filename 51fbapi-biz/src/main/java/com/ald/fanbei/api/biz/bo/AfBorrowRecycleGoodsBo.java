package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.List;

public class AfBorrowRecycleGoodsBo extends AbstractSerial {
    private Long id;
    private String name;

    private String goodsImg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
