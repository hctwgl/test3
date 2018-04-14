package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.dto.AfBorrowLegalCashCouponDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalCashCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalCashCouponDo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalCashCouponService;

import java.math.BigDecimal;
import java.util.List;


/**
 * 借款附带优惠券表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-14 16:31:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowLegalCashCouponService")
public class AfBorrowLegalCashCouponServiceImpl extends ParentServiceImpl<AfBorrowLegalCashCouponDo, Long> implements AfBorrowLegalCashCouponService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalCashCouponServiceImpl.class);
   
    @Resource
    private AfBorrowLegalCashCouponDao afBorrowLegalCashCouponDao;

		@Override
	public BaseDao<AfBorrowLegalCashCouponDo, Long> getDao() {
		return afBorrowLegalCashCouponDao;
	}

	@Override
	public Integer addBorrowLegalCashCoupon(AfBorrowLegalCashCouponDo afBorrowLegalCashCouponDo) {
		return afBorrowLegalCashCouponDao.saveRecord(afBorrowLegalCashCouponDo);
	}

	@Override
	public Integer deleteBorrowLegalCashCoupon(Long id) {
		return afBorrowLegalCashCouponDao.deleteById(id);
	}

	@Override
	public Integer updateBorrowLegalCashCoupon(AfBorrowLegalCashCouponDo afBorrowLegalCashCouponDo) {
		return afBorrowLegalCashCouponDao.updateById(afBorrowLegalCashCouponDo);
	}

	@Override
	public List<AfBorrowLegalCashCouponDto> getCouponIdByBorrowAmout(BigDecimal borrowAmount) {
		return afBorrowLegalCashCouponDao.getCouponIdByBorrowAmout(borrowAmount);
	}
}