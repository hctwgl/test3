package com.ald.fanbei.api.common.enums;

public enum  JsdBorrowCashReviewStatus {

	PASS("审核通过"),
	WAIT("待审批"),
	REFUSE("拒绝");

    public String desz;

    JsdBorrowCashReviewStatus(String desz) {
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
