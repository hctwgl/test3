package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfTradeBusinessInfoDao;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTradeOrderDao;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.biz.service.AfTradeOrderService;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 商圈订单扩展表ServiceImpl
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTradeOrderService")
public class AfTradeOrderServiceImpl extends ParentServiceImpl<AfTradeOrderDo, Long> implements AfTradeOrderService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTradeOrderServiceImpl.class);
   
    @Resource
    private AfTradeOrderDao afTradeOrderDao;
	@Resource
	private AfTradeBusinessInfoDao afTradeBusinessInfoDao;

	@Override
	public BaseDao<AfTradeOrderDo, Long> getDao() {
		return afTradeOrderDao;
	}

	@Override
	public BigDecimal getCanWithDrawMoney(Long businessId) {
		AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
		Date canWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(new Date()), 0 - infoDo.getWithdrawCycle());
		return afTradeOrderDao.getCanWithDrawMoney(businessId, canWithDrawDate);
	}

	@Override
	public BigDecimal getCannotWithDrawMoney(Long businessId) {
		AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
		Date now = new Date();
		Date cannotWithDrawDate;
		if (now.compareTo(DateUtil.getWithDrawOfDate(now)) > 0) {
			cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle());
		} else {
			cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle() - 1);
		}

		return afTradeOrderDao.getCannotWithDrawMoney(businessId, cannotWithDrawDate);
	}
	@Override
	public AfTradeOrderStatisticsDto payOrderInfo(Long businessId, Date startDate, Date endDate) {
		return afTradeOrderDao.payOrderInfo(businessId, startDate, endDate);
	}
}