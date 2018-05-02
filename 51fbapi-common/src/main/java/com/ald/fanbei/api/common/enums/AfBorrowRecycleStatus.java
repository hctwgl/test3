/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午5:15:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfBorrowRecycleStatus {
	AUDITING("AUDITING","审核中 ", "APPLY"),
    LOANING("LOANING","打款中","TRANSEDING"),
    WAITSEND("WAITSEND","待寄送","TRANSED"),
    PAYING("REPAYING","支付中","REPAYING"),
    TRANSEDFAIL("TRANSEDFAIL","打款失败","TRANSEDFAIL"),
    CLOSE("CLOSE","订单完成","FINSH"),
    FAIL("FAIL","订单失败","CLOSED")
	;
	public String code;
	public String desc;
	public String cashStatus;

    AfBorrowRecycleStatus(String code, String desc, String cashStatus) {
        this.code = code;
        this.desc = desc;
        this.cashStatus = cashStatus;

    }
    
    public static AfBorrowRecycleStatus findRoleTypeByCode(String code) {
        for (AfBorrowRecycleStatus roleType : AfBorrowRecycleStatus.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }
    
    public static AfBorrowRecycleStatus findByCashStatus(String status) {
        for (AfBorrowRecycleStatus unit : AfBorrowRecycleStatus.values()) {
            if (StringUtils.equals(status, unit.cashStatus)) {
                return unit;
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

}
