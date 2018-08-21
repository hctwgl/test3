package com.ald.fanbei.api.common.enums;

public enum  DsedNoticeStatus {

    FAIL("失败", "FAIL"),
    SUCCESS("成功", "SUCCESS");


    public String desz;
    public String code;

    DsedNoticeStatus(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
