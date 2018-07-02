package com.ald.fanbei.api.common.enums;

import lombok.Getter;

/**
 * 个人拆红包的红包来源类型
 *
 * @author wangli
 * @date 2018/5/7 10:42
 */
@Getter
public enum  SelfOpenRedPacketSourceType {

    OPEN_SELF("OPEN_SELF", "自拆"), OPEN_SHARE_MOMENTS("OPEN_SHARE_MOMENTS", "朋友圈分享"),
    OPEN_SHARE_FRIEND("OPEN_SHARE_FRIEND", "OPEN_SHARE_FRIEND")
    ;

    private String code;

    private String name;

    private SelfOpenRedPacketSourceType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
