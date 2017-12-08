package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfCouponDouble12Dao;
import com.ald.fanbei.api.dal.domain.AfCouponDouble12Do;
import com.ald.fanbei.api.biz.service.AfCouponDouble12Service;



/**
 * 双十二ServiceImpl
 * 
 * @author yanghailong_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:28:44
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afCouponDouble12Service")
public class AfCouponDouble12ServiceImpl extends ParentServiceImpl<AfCouponDouble12Do, Long> implements AfCouponDouble12Service {
	
    private static final Logger logger = LoggerFactory.getLogger(AfCouponDouble12ServiceImpl.class);
   
    @Resource
    private AfCouponDouble12Dao afCouponDouble12Dao;

		@Override
	public BaseDao<AfCouponDouble12Do, Long> getDao() {
		return afCouponDouble12Dao;
	}

	@Override
	public List<AfCouponDouble12Do> getCouponList() {
		// TODO Auto-generated method stub
		return afCouponDouble12Dao.getCouponList();
	}

	@Override
	public AfCouponDouble12Do getCouponByCouponId(Long couponId) {
		// TODO Auto-generated method stub
		return afCouponDouble12Dao.getCouponByCouponId(couponId);
	}

	@Override
	public void updateCountById(AfCouponDouble12Do afCouponDouble12Do) {
		// TODO Auto-generated method stub
		afCouponDouble12Dao.updateCountById(afCouponDouble12Do);
	}

	@Override
	public void updateReCountById(AfCouponDouble12Do afCouponDouble12Do) {
		// TODO Auto-generated method stub
		afCouponDouble12Dao.updateReCountById(afCouponDouble12Do);
	}
}