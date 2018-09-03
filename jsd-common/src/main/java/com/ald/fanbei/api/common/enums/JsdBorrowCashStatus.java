package com.ald.fanbei.api.common.enums;

public enum  JsdBorrowCashStatus {

    APPLY("申请/未审核", "APPLY"),
    TRANSFERING("打款中", "TRANSEDING"),
    TRANSFERRED("已经打款/待还款", "TRANSED"),
    TRANSEDFAIL("打款失败", "TRANSEDFAIL"),
    REPAYING("还款中", "REPAYING"),
    FINISHED("已结清", "FINISHED"),
    CLOSED("关闭", "CLOSED");

    public String desz;
    public String code;

    JsdBorrowCashStatus(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }

    /**
     * @return the desz
     */
    public String getDesz() {
        return desz;
    }

    /**
     * @param desz the desz to set
     */
    public void setDesz(String desz) {
        this.desz = desz;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

}
