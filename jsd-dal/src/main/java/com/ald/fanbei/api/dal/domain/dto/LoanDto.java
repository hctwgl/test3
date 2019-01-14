package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

public class LoanDto extends JsdBorrowCashDo {
    private String realName;//借款人姓名
    private String mobile;//借款人手机号码
    private String isRenewal;//是否续借

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIsRenewal() {
        return isRenewal;
    }

    public void setIsRenewal(String isRenewal) {
        this.isRenewal = isRenewal;
    }
}
