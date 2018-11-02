package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;


/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowLegalOrderCashService")
public class JsdBorrowLegalOrderCashServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderCashDo, Long> implements JsdBorrowLegalOrderCashService {
    @Resource
    private JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;

		@Override
	public BaseDao<JsdBorrowLegalOrderCashDo, Long> getDao() {
		return jsdBorrowLegalOrderCashDao;
	}

	@Override
	public JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(borrowId);
	}

	@Override
	public BigDecimal calculateLegalRestAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo) {
		BigDecimal restAmount = BigDecimal.ZERO;
		if(cashDo != null) {
			restAmount = BigDecimalUtil.add(restAmount, cashDo.getAmount(),
					cashDo.getOverdueAmount(), cashDo.getSumRepaidOverdue(),
					cashDo.getInterestAmount(),cashDo.getSumRepaidInterest(),
					cashDo.getPoundageAmount(),cashDo.getSumRepaidPoundage())
					.subtract(cashDo.getRepayAmount());
		}
		if(orderCashDo != null) {
			restAmount = BigDecimalUtil.add(restAmount, orderCashDo.getAmount(),
					orderCashDo.getOverdueAmount(), orderCashDo.getSumRepaidOverdue(),
					orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidInterest(),
					orderCashDo.getPoundageAmount(),orderCashDo.getSumRepaidPoundage())
					.subtract(orderCashDo.getRepaidAmount());
		}
		return restAmount;
	}

	@Override
	public JsdBorrowLegalOrderCashDo getOverdueBorrowLegalOrderCashByBorrowId(Long borrowId) {
		Date now=new Date(System.currentTimeMillis());
        Date bengin = DateUtil.getStartOfDate(now);
		return jsdBorrowLegalOrderCashDao.getOverdueBorrowLegalOrderCashByBorrowId(borrowId,bengin);
	}

	@Override
	public JsdBorrowLegalOrderCashDo getLastOrderCashByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(borrowId);
	}

	@Override
	public JsdBorrowLegalOrderCashDo getFirstOrderCashByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getFirstOrderCashByBorrowId(borrowId);
	}

	@Override
	public List<JsdBorrowLegalOrderCashDo> getBorrowOrderCashsByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getBorrowOrderCashsByBorrowId(borrowId);
	}

	@Override
	public JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashByOrderId(Long orderId){
		return jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByOrderId(orderId);
	}

	@Override
	public JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashDateBeforeToday(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashDateBeforeToday(borrowId);
	}

	@Override
	public JsdBorrowLegalOrderCashDo getLegalOrderByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderCashDao.getLegalOrderByBorrowId(borrowId);
	}

}