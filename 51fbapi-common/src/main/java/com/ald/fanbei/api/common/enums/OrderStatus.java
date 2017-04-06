package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午15:31:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OrderStatus {
	
	NEW("NEW", "新建/待付款"),
	DEALING("DEALING", "支付中"),
	PAID("PAID", "已支付/待收货"),
	FINISHED("FINISHED", "已收货/订单完成"),
	REBATED("REBATED", "返利成功"),
	CLOSED("CLOSED", "订单关闭(未付款或退款成功)"),
	WAITING_REFUND("WAIT REFUND", "等待退款"),
	DEAL_REFUNDING("REFUNDING", "退款中");
	
    private String code;
    private String name;

    private static Map<String,OrderStatus> codeRoleTypeMap = null;

    OrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderStatus findRoleTypeByCode(String code) {
        for (OrderStatus roleType : OrderStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,OrderStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, OrderStatus>();
        for(OrderStatus item:OrderStatus.values()){
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
