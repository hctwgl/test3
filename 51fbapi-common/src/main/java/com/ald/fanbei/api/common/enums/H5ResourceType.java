package com.ald.fanbei.api.common.enums;

import lombok.Getter;

/**
 * h5资源配置枚举
 *
 * @author wangli
 * @date 2018/5/17 9:26
 */
@Getter
public enum H5ResourceType {

    MINE_HOME_LOVE_SHOP("MINE_HOME", 1, "个人中心爱花"),
    MINE_HOME_SIGN_IMG("MINE_HOME", 2, "签到图片")
    ;

    // h5资源标签
    private String tag;

    // 排序，实际上是作为页面中的位置标识
    private Integer sort;

    private String description;

    H5ResourceType(String tag, Integer sort, String description) {
        this.tag = tag;
        this.sort = sort;
        this.description = description;
    }
}
