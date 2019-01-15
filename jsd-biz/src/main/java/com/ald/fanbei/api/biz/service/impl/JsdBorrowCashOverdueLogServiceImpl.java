package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.dto.JsdBorrowCashOverdueLogDto;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdBorrowCashOverdueLogService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashOverdueLogDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;


/**
 * ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-04 11:10:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashOverdueLogService")
public class JsdBorrowCashOverdueLogServiceImpl extends ParentServiceImpl<JsdBorrowCashOverdueLogDo, Long> implements JsdBorrowCashOverdueLogService {
	
    @Resource
    private JsdBorrowCashOverdueLogDao jsdBorrowCashOverdueLogDao;

		@Override
	public BaseDao<JsdBorrowCashOverdueLogDo, Long> getDao() {
		return jsdBorrowCashOverdueLogDao;
	}

	@Override
	public int getBorrowCashOverDueLogByNow(String borrowId) {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return jsdBorrowCashOverdueLogDao.getBorrowCashOverDueLogByNow(borrowId,bengin);
	}

	@Override
	public List<JsdBorrowCashOverdueLogDto> getListOrderCashOverdueLogByBorrowId(Long borrowId, Date payTime) {
		return jsdBorrowCashOverdueLogDao.getListOrderCashOverdueLogByBorrowId(borrowId,payTime);
	}

	@Override
	public List<JsdBorrowCashOverdueLogDo> getListCashOverdueLog(Date payTime) {
		return jsdBorrowCashOverdueLogDao.getListCashOverdueLog(payTime);
	}

	@Override
	public List<JsdBorrowCashOverdueLogDto> getListCashOverdueLogByBorrowId(Long borrowId, Date payTime) {
		return jsdBorrowCashOverdueLogDao.getListCashOverdueLogByBorrowId(borrowId,payTime);
	}

	@Override
	public int getBorrowCashOverDueLogToDay() {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return jsdBorrowCashOverdueLogDao.getBorrowCashOverDueLogToDay(bengin);
	}
}