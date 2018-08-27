package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

public enum JsdBorrowLegalOrderCashStatus {
    APPLYING("APPLYING","APPLYING", "申请/未审核 "),
    AWAIT_REPAY("AWAIT_REPAY", "AWAIT_REPAY","待还款"),
    PART_REPAID("PART_REPAID","PART_REPAID", "部分还款"),
    FINISHED("FINISHED", "FINISHED","已结清"),
    CLOSED("CLOSED", "CLOSED","关闭");


    private String code;
    private String name;
    private String dec;


    JsdBorrowLegalOrderCashStatus(String code, String name,String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static JsdBorrowLegalOrderCashStatus findRoleTypeByCode(String code) {
        for (JsdBorrowLegalOrderCashStatus roleType : JsdBorrowLegalOrderCashStatus.values()) {
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
