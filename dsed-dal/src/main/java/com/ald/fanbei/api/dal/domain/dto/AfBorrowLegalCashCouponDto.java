package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalCashCouponDo;

/**
 * 借款附带优惠券表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-14 16:31:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowLegalCashCouponDto extends AfBorrowLegalCashCouponDo {

    private static final long serialVersionUID = 1L;

    private String couponName;

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }
}