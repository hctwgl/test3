package com.ald.fanbei.api.common.enums;

public enum  JsdNoticeStatus {

    FAIL("失败", "FAIL"),
    SUCCESS("成功", "SUCCESS");


    public String desz;
    public String code;

    JsdNoticeStatus(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
