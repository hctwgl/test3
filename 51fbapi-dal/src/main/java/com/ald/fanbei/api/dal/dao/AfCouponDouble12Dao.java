package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfCouponDouble12Do;

/**
 * 双十二Dao
 * 
 * @author yanghailong_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:28:44
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponDouble12Dao extends BaseDao<AfCouponDouble12Do, Long> {

	List<AfCouponDouble12Do> getCouponList();

	AfCouponDouble12Do getCouponByCouponId(Long couponId);

	void updateCountById(AfCouponDouble12Do afCouponDouble12Do);

	void updateReCountById(AfCouponDouble12Do afCouponDouble12Do);

    

}
