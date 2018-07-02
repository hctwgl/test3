package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxiao @date 2018/5/15 15:08
 * @类描述：任务子类型
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
// 子任务类型：commodity：商品；brand：品牌；classification：分类；activity：活动
public enum AfTaskSecType {
    COMMODITY("commodity","商品"),
    BRAND("brand","品牌"),
    CATEGORY("category","分类"),
    ACTIVITY("activity","活动"),
    QUANTITY("quantity","数量");

    private String code;

    private String description;

    private static Map<String, AfTaskSecType> codeMap = null;

    AfTaskSecType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AfTaskSecType findEnumByCode(String code) {
        for (AfTaskSecType taskSecType : AfTaskSecType.values()) {
            if (taskSecType.getCode().equals(code)) {
                return taskSecType;
            }
        }
        return null;
    }

    public static Map<String, AfTaskSecType> getCodeEnumMap() {
        if (codeMap != null && codeMap.size() > 0) {
            return codeMap;
        }
        codeMap = new HashMap<String, AfTaskSecType>();
        for (AfTaskSecType item : AfTaskSecType.values()) {
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
