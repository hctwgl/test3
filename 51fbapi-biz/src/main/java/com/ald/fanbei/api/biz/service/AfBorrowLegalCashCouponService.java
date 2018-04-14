package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalCashCouponDo;

/**
 * 借款附带优惠券表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-14 16:31:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalCashCouponService extends ParentService<AfBorrowLegalCashCouponDo, Long>{

    Integer addBorrowLegalCashCoupon(AfBorrowLegalCashCouponDo afBorrowLegalCashCouponDo);

    Integer deleteBorrowLegalCashCoupon(Long id);

    Integer updateBorrowLegalCashCoupon(AfBorrowLegalCashCouponDo afBorrowLegalCashCouponDo);
}
