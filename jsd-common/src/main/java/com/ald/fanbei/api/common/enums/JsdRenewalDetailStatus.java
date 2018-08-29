package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum  JsdRenewalDetailStatus {

    APPLY("A", "新建状态"),
    PROCESS("P", "处理中"),
    SMS("S", "快捷支付发完短信"),
    NO("N", "续期失败"),
    YES("Y", "续期成功");

    private String code;
    private String name;

    private static Map<String, JsdRenewalDetailStatus> codeRenewalTypeMap = null;

    JsdRenewalDetailStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static JsdRenewalDetailStatus findRenewalTypeByCode(String code) {
        for (JsdRenewalDetailStatus renewalType : JsdRenewalDetailStatus.values()) {
            if (renewalType.getCode().equals(code)) {
                return renewalType;
            }
        }
        return null;
    }

    public static Map<String, JsdRenewalDetailStatus> getCodeRenewalTypeMap() {
        if (codeRenewalTypeMap != null && codeRenewalTypeMap.size() > 0) {
            return codeRenewalTypeMap;
        }
        codeRenewalTypeMap = new HashMap<String, JsdRenewalDetailStatus>();
        for (JsdRenewalDetailStatus item : JsdRenewalDetailStatus.values()) {
            codeRenewalTypeMap.put(item.getCode(), item);
        }
        return codeRenewalTypeMap;
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
