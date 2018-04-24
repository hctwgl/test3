package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalCouponDo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalCouponService;



/**
 * 合规优惠券记录ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-04-13 13:58:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowLegalCouponService")
public class AfBorrowLegalCouponServiceImpl extends ParentServiceImpl<AfBorrowLegalCouponDo, Long> implements AfBorrowLegalCouponService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalCouponServiceImpl.class);
   
    @Resource
    private AfBorrowLegalCouponDao afBorrowLegalCouponDao;

		@Override
	public BaseDao<AfBorrowLegalCouponDo, Long> getDao() {
		return afBorrowLegalCouponDao;
	}

	@Override
	public AfBorrowLegalCouponDo getByRenewalId(Long renewalId) {
		return afBorrowLegalCouponDao.getByRenewalId(renewalId);
	}

	@Override
	public AfBorrowLegalCouponDo getByBorrowId(Long borrowId) {
		return afBorrowLegalCouponDao.getByBorrowId(borrowId);
	}
}