package com.ald.fanbei.api.common.enums;

/**
 * @ClassName GoodsSourceEnum.
 * @desc
 * 商品来源枚举
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/9/2 16:43
 */
public enum GoodsSourceEnum {

    /**商品来源-淘宝**/
    GOODS_SOURCE_TAOBAO("TAOBAO"),
    /**商品来源-天猫**/
    GOODS_SOURCE_TMALL("TMALL"),
    /**商品来源-京东**/
    GOODS_SOURCE_JD("JD");



    public String code;

    GoodsSourceEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
