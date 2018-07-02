/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：售后申请状态
 * @author chengkang 2017年7月8日下午6:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAftersaleApplyStatus {
	NEW("NEW", "新建"),
	CLOSE("CLOSE","用户关闭售后申请"),
	NOTPASS("NOTPASS","审核不通过"),
	WAIT_GOODS_BACK("WAIT_GOODS_BACK","审核通过待回寄商品"),
	GOODS_BACKIING("GOODS_BACKIING","商品回寄中"),
	WAIT_REFUND("WAIT_REFUND","等待退款"),
	REFUNDING("REFUNDING","等待退款"),
	FINISH("FINISH","退款完成");
    
    private String code;
    private String name;

    private static Map<String,AfAftersaleApplyStatus> codeRoleTypeMap = null;

    AfAftersaleApplyStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfAftersaleApplyStatus findRoleTypeByCode(String code) {
        for (AfAftersaleApplyStatus roleType : AfAftersaleApplyStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfAftersaleApplyStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfAftersaleApplyStatus>();
        for(AfAftersaleApplyStatus item:AfAftersaleApplyStatus.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
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
