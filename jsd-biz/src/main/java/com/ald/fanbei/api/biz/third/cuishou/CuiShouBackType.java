package com.ald.fanbei.api.biz.third.cuishou;

/**
 * 返回状态
 */
public enum CuiShouBackType {
    synchronization("synchronization", "同步"),
    asynchronous("asynchronous", "异步");

    private String code;
    private String name;

    CuiShouBackType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
