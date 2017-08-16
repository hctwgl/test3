package com.ald.fanbei.api.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：app订单列表查询状态汇总
 * @author chengkang 2017年7月13日下午17:31:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AppOrderSearchStatus {
	ALL("ALL", "全部订单"),
	NEW("NEW", "待支付"),
	DEALING("DEALING", "支付中"),
	TOREVIEW("TOREVIEW", "待审核"),
	REVIEWING("REVIEWING", "审核中"),
	TODELIVER("TODELIVER", "待发货"),
	DELIVERED("DELIVERED", "待收货"),
	TOREBATE("TOREBATE", "待返利"),
	FINISHED("FINISHED", "订单完成"),
	CLOSED("CLOSED", "订单关闭"),
	WAIT_REFUND("WAIT_REFUND", "售后处理"),
	DEAL_REFUNDING("DEAL_REFUNDING", "退款中");
	
    private String code;
    private String name;

    private static Map<String,AppOrderSearchStatus> codeRoleTypeMap = null;
    private static List<String> codes = null;

    AppOrderSearchStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AppOrderSearchStatus findRoleTypeByCode(String code) {
        for (AppOrderSearchStatus roleType : AppOrderSearchStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    public static List<String> getAllCodes(){
    	if(codes != null && codes.size() > 0){
            return codes;
        }
    	codes = new ArrayList<String>();
        for(AppOrderSearchStatus item:AppOrderSearchStatus.values()){
        	codes.add(item.getCode());
        }
        return codes;
    }
    
    public static Map<String,AppOrderSearchStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AppOrderSearchStatus>();
        for(AppOrderSearchStatus item:AppOrderSearchStatus.values()){
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
