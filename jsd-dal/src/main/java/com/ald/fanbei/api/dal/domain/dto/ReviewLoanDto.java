package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

public class ReviewLoanDto extends JsdBorrowCashDo {
    private String realName;//借款人姓名
    private String mobile;//借款人手机号码

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
}
