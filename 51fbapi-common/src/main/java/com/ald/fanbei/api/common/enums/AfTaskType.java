package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxiao @date 2018/5/15 15:02
 * @类描述：任务类型
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfTaskType {
    BROWSE("browse","浏览"),
    SHOPPING("shopping","购物"),
    SHARE("share","分享"),
    VERIFIED("verified","实名"),
    STRONG_RISK("strong_risk","强风控通过"),
    PUSH("push","消息推送"),
    LOAN_MARKET_ACCESS("loan_market_access","借贷超市注册");


    private String code;

    private String description;

    private static Map<String, AfTaskType> codeMap = null;

    AfTaskType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AfTaskType findEnumByCode(String code) {
        for (AfTaskType taskType : AfTaskType.values()) {
            if (taskType.getCode().equals(code)) {
                return taskType;
            }
        }
        return null;
    }

    public static Map<String, AfTaskType> getCodeEnumMap() {
        if (codeMap != null && codeMap.size() > 0) {
            return codeMap;
        }
        codeMap = new HashMap<String, AfTaskType>();
        for (AfTaskType item : AfTaskType.values()) {
            codeMap.put(item.getCode(), item);
        }
        return codeMap;
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
