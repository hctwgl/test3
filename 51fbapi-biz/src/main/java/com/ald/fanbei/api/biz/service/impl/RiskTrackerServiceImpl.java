package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.RiskTrackerDao;
import com.ald.fanbei.api.dal.domain.RiskTrackerDo;
import com.ald.fanbei.api.biz.service.RiskTrackerService;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;


/**
 * 风控调用追踪ServiceImpl
 * 
 * @author 任春雷
 * @version 1.0.0 初始化
 * @date 2017-11-07 21:36:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("riskTrackerService")
public class RiskTrackerServiceImpl extends ParentServiceImpl<RiskTrackerDo, Long> implements RiskTrackerService {
	
    private static final Logger logger = LoggerFactory.getLogger(RiskTrackerServiceImpl.class);
	@Resource
    TransactionTemplate transactionTemplate;
    @Resource
    private RiskTrackerDao riskTrackerDao;

		@Override
	public BaseDao<RiskTrackerDo, Long> getDao() {
		return riskTrackerDao;
	}

	@Override
	public int test11() {
		final  RiskTrackerDo riskTrackerDo1=  riskTrackerDao.getById(2l);
		transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(TransactionStatus transactionStatus) {
				RiskTrackerDo riskTrackerDo=  riskTrackerDao.getById(2l);
				riskTrackerDo.setResult("123");
				riskTrackerDo1.setTrackId("1112");
				riskTrackerDao.updateById(riskTrackerDo);
				return null;
			}
		});
		logger.error("----"+riskTrackerDo1.getTrackId());
		return 1;
	}
}