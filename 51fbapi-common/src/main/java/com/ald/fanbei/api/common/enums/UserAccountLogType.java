package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月22日下午16:12:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UserAccountLogType {

	CASH("CASH", "取现"), 
	JIFENBAO("JIFENBAO", "集分宝取现"), 
	BorrowCash("BORROWCASH", "借钱"), 

	AU_SCORE("AU_SCORE", "授权分数"),
	AU_AMOUNT("AU_AMOUNT", "授权金额"),
	FREEZE("FREEZE", "冻结金额"),
	REBATE("REBATE", "返利"), 
	SIGN("SIGN", "签到"),
	REGIST("REGIST", "新注册"),
	CONSUME("CONSUME", "分期"),
	
	WX_REFUND("WX_REFUND","微信退款"),
	AP_REFUND("AP_REFUND", "代付退款"),
	BANK_REFUND("BANK_REFUND", "银行卡退款"),
	REPAYMENT("REPAYMENT", "还款"),
	REPAYMENTCASH("REPAYMENTCASH", "借钱还款"),
	RENEWAL_PAY("RENEWAL_PAY", "续期支付"),

	REBATE_CASH("REBATE_CASH", "返利现金"),
	REBATE_JFB("REBATE_JFB", "返利集分宝"),
    AUTHNAME("AUTHNAME", "实名认证");

    private String code;
    private String name;

    private static Map<String,UserAccountLogType> codeRoleTypeMap = null;

    UserAccountLogType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserAccountLogType findRoleTypeByCode(String code) {
        for (UserAccountLogType roleType : UserAccountLogType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,UserAccountLogType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, UserAccountLogType>();
        for(UserAccountLogType item:UserAccountLogType.values()){
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
