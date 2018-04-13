package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalCouponDo;

/**
 * 合规优惠券记录Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-04-13 13:58:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalCouponDao extends BaseDao<AfBorrowLegalCouponDo, Long> {

	AfBorrowLegalCouponDo getByRenewalId(Long renewalId);

    

}
