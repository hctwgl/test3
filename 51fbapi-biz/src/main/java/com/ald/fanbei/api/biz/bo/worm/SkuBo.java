package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SkuBo.
 * @desc
 * 第三方自建商城sku项BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 0:56
 */
public class SkuBo implements Serializable{

    private static final long serialVersionUID = 7702000862824032784L;

    private String skuKey;  //sku参数名

    private List<SkuDetailBo> skuValueList; //sku详情

    public String getSkuKey() {
        return skuKey;
    }

    public void setSkuKey(String skuKey) {
        this.skuKey = skuKey;
    }

    public List<SkuDetailBo> getSkuValueList() {
        return skuValueList;
    }

    public void setSkuValueList(List<SkuDetailBo> skuValueList) {
        this.skuValueList = skuValueList;
    }
}
