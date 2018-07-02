package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName SkuGroupBo.
 * @desc
 * 第三方自建商城SKU组合项BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 1:08
 */
public class SkuGroupBo implements Serializable {
    private static final long serialVersionUID = 8133459331352369202L;

    private String skuGroup;    //sku组合编码(skuID,skuID)

    private String stockGoods;  //库存量

    private BigDecimal coutPrice;   //sku组合项原价

    private BigDecimal price;   //sku组合项现价

    private String skuGroupValue;   //sku组合项显示值

    private String skuGroupId;   //sku组合项id

    public String getSkuGroup() {

        return skuGroup;
    }

    public void setSkuGroup(String skuGroup) {
        this.skuGroup = skuGroup;
    }

    public String getStockGoods() {
        return stockGoods;
    }

    public void setStockGoods(String stockGoods) {
        this.stockGoods = stockGoods;
    }

    public BigDecimal getCoutPrice() {
        return coutPrice;
    }

    public void setCoutPrice(BigDecimal coutPrice) {
        this.coutPrice = coutPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSkuGroupValue() {
        return skuGroupValue;
    }

    public void setSkuGroupValue(String skuGroupValue) {
        this.skuGroupValue = skuGroupValue;
    }

    public String getSkuGroupId() {
        return skuGroupId;
    }

    public void setSkuGroupId(String skuGroupId) {
        this.skuGroupId = skuGroupId;
    }
}
