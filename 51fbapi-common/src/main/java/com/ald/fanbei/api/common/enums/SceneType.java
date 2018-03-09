package com.ald.fanbei.api.common.enums;

public enum SceneType {

    ONLINE("ONLINE", "21","线上分期"),
    TRAIN("TRAIN", "22","线下培训"),
    CASH("CASH", "20","现金"),
    BLD_LOAN("BLD_LOAN","23","白领贷"),
	LOAN_TOTAL("LOAN_TOTAL","0","借钱总额度");
   
    private String name;
    private String code;
    private String description;

    SceneType(String name,String code,String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static SceneType findSceneTypeByCode(String code) {
        for (SceneType cceneType : SceneType.values()) {
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
