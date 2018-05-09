package com.ald.fanbei.api.common.enums.recycle;

public enum AfRecycleGoodsStatus {

    OPEN("open", "打开"),
    OFF("off", "关闭"),
    ;

    private String code;

    private String description;

    AfRecycleGoodsStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AfRecycleOrderType findEnumByCode(Integer code) {
        for (AfRecycleOrderType goodSource : AfRecycleOrderType.values()) {
            if (goodSource.getCode().equals(code)) {
                return goodSource;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
