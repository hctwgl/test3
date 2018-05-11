package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashPushDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashPushDo;
import com.ald.fanbei.api.biz.service.AfBorrowCashPushService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-03-01 18:59:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowCashPushService")
public class AfBorrowCashPushServiceImpl extends ParentServiceImpl<AfBorrowCashPushDo, Long> implements AfBorrowCashPushService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowCashPushServiceImpl.class);
   
    @Resource
    private AfBorrowCashPushDao afBorrowCashPushDao;

		@Override
	public BaseDao<AfBorrowCashPushDo, Long> getDao() {
		return afBorrowCashPushDao;
	}

	@Override
	public void saveOrUpdate(AfBorrowCashPushDo borrowCashPush) {
		AfBorrowCashPushDo borrowCashPushTemp = afBorrowCashPushDao.getByBorrowCashId(borrowCashPush.getBorrowCashId());
		if (borrowCashPushTemp == null) {
			afBorrowCashPushDao.saveRecord(borrowCashPush);
		} else {
			borrowCashPush.setRid(borrowCashPushTemp.getRid());
			afBorrowCashPushDao.updateById(borrowCashPush);
		}
	}

	@Override
	public AfBorrowCashPushDo getByBorrowCashId(Long rid) {
		return afBorrowCashPushDao.getByBorrowCashId(rid);
	}
}