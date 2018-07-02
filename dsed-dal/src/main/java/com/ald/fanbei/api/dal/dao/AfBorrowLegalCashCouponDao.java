package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalCashCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowLegalCashCouponDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 借款附带优惠券表Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-14 16:31:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalCashCouponDao extends BaseDao<AfBorrowLegalCashCouponDo, Long> {

    int deleteById(Long id);

    List<AfBorrowLegalCashCouponDto> getCouponIdByBorrowAmout(BigDecimal borrowAmount);

}
