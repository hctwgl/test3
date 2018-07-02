package com.ald.fanbei.api.common.enums;

import lombok.Getter;

/**
 * 用户关联的第三方类型
 *
 * @author wangli
 * @date 2018/5/4 9:27
 */
@Getter
public enum UserThirdType {

    WX("WX", "微信")
    ;

    private String code;

    private String name;

    private UserThirdType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
