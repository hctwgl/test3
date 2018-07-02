package com.ald.fanbei.api.biz.third.util.cuishou;

public enum  CuiShouType {
    BORROW("BORROW", "线上消费分期"),
    BORROW_CASH("BORROW_CASH", "现金贷"),
    WITH_BORROW("WITH_BORROW", "白领贷"),
    BORROW_RENEWAL("BORROW_RENEWAL", "现金货续期");

    private String code;
    private String name;

    CuiShouType(String code, String name) {
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
