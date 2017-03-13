package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午2:14:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BorrowStatus {

	APPLY("APPLY", "未审核"), 
	AGREE("AGREE", "审核通过"),
	CANCEL("CANCEL", "审核取消"),
	TRANSED("TRANSED","转账成功"),
	CLOSE("CLOSE", "审核关闭");
    
    private String code;
    private String name;

    private static Map<String,BorrowStatus> codeRoleTypeMap = null;

    BorrowStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BorrowStatus findRoleTypeByCode(String code) {
        for (BorrowStatus roleType : BorrowStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BorrowStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BorrowStatus>();
        for(BorrowStatus item:BorrowStatus.values()){
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
