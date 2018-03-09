package com.ald.fanbei.api.common.enums;

public enum WeakRiskSceneType {

    ONLINE("ONLINE", "40","线上分期"),
    CASH("CASH", "50","现金"),
    BLD_LOAN("BLD_LOAN","140","白领贷");

    private String code;
    private String name;
    private String description;

    WeakRiskSceneType(String name,String code,String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static WeakRiskSceneType findSceneTypeByCode(String code) {
        for (WeakRiskSceneType cceneType : WeakRiskSceneType.values()) {
            if (cceneType.getCode().equals(code)) {
                return cceneType;
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
