package com.ald.fanbei.api.common.enums;

public enum UserAuthSceneStatus {

    //Y已认证 N未认证 C认证失败 P未认证且无过期 A认证中
    YES("YES", "Y","已认证"),
    NO("NO", "N","未认证"),
    FAILED("FAILED", "C","认证失败"),
    PASSING("PASSING", "P","未认证且无过期"),
    CHECKING("CHECKING", "A","认证中"),;

    private String name;
    private String code;
    private String description;

    UserAuthSceneStatus(String name,String code,String description) {
        this.code = code;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
