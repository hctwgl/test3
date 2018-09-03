package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;


/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashService")
public class JsdBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements JsdBorrowCashService {
    @Resource
    private JsdBorrowCashDao jsdBorrowCashDao;

		@Override
	public BaseDao<JsdBorrowCashDo, Long> getDao() {
		return jsdBorrowCashDao;
	}

	@Override
	public JsdBorrowCashDo getByBorrowNo(String borrowNo) {
		return jsdBorrowCashDao.getByBorrowNo(borrowNo);
	}

	@Override
	public boolean isCanBorrowCash(Long userId) {
		List<JsdBorrowCashDo> notFinishBorrowList = jsdBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
		return notFinishBorrowList.isEmpty();
	}

	@Override
	public String getCurrentLastBorrowNo(String orderNoPre) {
		return jsdBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
	}

	@Override
	public void dealBorrowSucc(Long cashId, String outTradeNo) {
		
	}

	@Override
	public void dealBorrowFail(Long cashId, String outTradeNo, String failMsg) {
		
	}

}