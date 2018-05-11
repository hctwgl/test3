package com.ald.fanbei.api.common.enums;

/**
 * 底部商品页页面标识
 *
 * @author wangli
 * @date 2018/4/11 8:33
 */
public enum BottomGoodsPageFlag {

    LIFE("LIFE", "生活页");

    private String code;

    private String name;

    BottomGoodsPageFlag(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
