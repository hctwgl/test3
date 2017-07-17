package com.ald.fanbei.api.common.enums;

/**
 * 是否免息账单标识
 *
 * @author hantao
 * @desc
 */
public enum InterestfreeCode {

    /**
     * 完全免息账单
     **/
    IS_FREE("1"),
    /**
     * 部分免息账单
     **/
    HALF_FREE("2"),
    /**
     * 非免息账单
     **/
    NO_FREE("0");

    private String code;

    InterestfreeCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
