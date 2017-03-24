package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午9:19:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PayStatus {
	
	N("NEW", "新建/待付款"),
	PAID("PAID", "已支付/待收货"),
	FINISHED("FINISHED", "已收货/订单完成"),
	REBATED("REBATED", "返利成功"),
	CLOSED("CLOSED", "订单关闭(未付款或退款成功)"),
	WAITING_REFUND("WAITING_REFUND", "等待退款"),
	DEAL_REFUNDING("DEAL_REFUNDING", "退款中"),
	REFUND_COMPLETE("REFUND_COMPLETE", "退款完成");
	
    private String code;
    private String name;

    private static Map<String,MobileStatus> codeRoleTypeMap = null;

    PayStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MobileStatus findRoleTypeByCode(String code) {
        for (MobileStatus roleType : MobileStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,MobileStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, MobileStatus>();
        for(MobileStatus item:MobileStatus.values()){
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
