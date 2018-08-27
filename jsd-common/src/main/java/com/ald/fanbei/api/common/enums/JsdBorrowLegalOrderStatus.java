package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

public enum JsdBorrowLegalOrderStatus {
	UNPAID("UNPAID","UNPAID", "未支付"),
	AWAIT_DELIVER("AWAIT_DELIVER", "AWAIT_DELIVER","待发货"),
	DELIVERED("DELIVERED","DELIVERED", "已发货"),
	CONFIRM_RECEIVED("CONFIRM_RECEIVED", "CONFIRM_RECEIVED","已确认收货"),
    CLOSED("CLOSED", "CLOSED","订单关闭");


    private String code;
    private String name;
    private String dec;


    JsdBorrowLegalOrderStatus(String code, String name,String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static JsdBorrowLegalOrderStatus findRoleTypeByCode(String code) {
        for (JsdBorrowLegalOrderStatus roleType : JsdBorrowLegalOrderStatus.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dec
     */
    public String getDec() {
        return dec;
    }

    /**
     * @param dec the dec to set
     */
    public void setDec(String dec) {
        this.dec = dec;
    }
}
