package com.ald.fanbei.api.common.enums;

public enum  JsdBorrowCashStatus {

    APPLY("申请/未审核"),
    TRANSFERING("打款中"),
    TRANSFERRED("已经打款/待还款"),
    TRANSEDFAIL("打款失败"),
    REPAYING("还款中"),
    FINISHED("已结清"),
    CLOSED("关闭");

    public String desz;

    JsdBorrowCashStatus(String desz) {
        this.desz = desz;
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
}
