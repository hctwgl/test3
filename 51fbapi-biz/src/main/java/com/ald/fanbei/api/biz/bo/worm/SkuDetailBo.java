package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;

/**
 * @ClassName SkuDetailBo.
 * @desc
 * 第三方自建商城sku项详情BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 0:57
 */
public class SkuDetailBo implements Serializable {
    private static final long serialVersionUID = -3580985608156575000L;

    private String skuValue;    //sku参数值

    private String skuValueAlt; //sku参数alt值

    private String skuValueId;    //sku参数ID

    private String skuType;    //sku参数类型	0文字 1图片

    public String getSkuValue() {
        return skuValue;
    }

    public void setSkuValue(String skuValue) {
        this.skuValue = skuValue;
    }

    public String getSkuValueAlt() {
        return skuValueAlt;
    }

    public void setSkuValueAlt(String skuValueAlt) {
        this.skuValueAlt = skuValueAlt;
    }

    public String getSkuValueId() {
        return skuValueId;
    }

    public void setSkuValueId(String skuValueId) {
        this.skuValueId = skuValueId;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }
}
