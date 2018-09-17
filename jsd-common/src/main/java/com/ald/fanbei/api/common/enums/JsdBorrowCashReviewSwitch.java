package com.ald.fanbei.api.common.enums;

public enum  JsdBorrowCashReviewSwitch {

	AUTO("自动"),
	MANUAL("手动"),
	SEMI_AUTO("半自动");

    public String desz;

    JsdBorrowCashReviewSwitch(String desz) {
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
