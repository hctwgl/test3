package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashOverdueLogDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashOverdueLogService;

import java.util.Date;


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
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowCashOverdueLogServiceImpl.class);
   
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
}