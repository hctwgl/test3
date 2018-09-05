package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;

import java.math.BigDecimal;
import java.util.Date;


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
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowLegalOrderCashServiceImpl.class);
   
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
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
					cashDo.getRateAmount(),cashDo.getSumRate(),
					cashDo.getPoundage(),cashDo.getSumRenewalPoundage())
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
}