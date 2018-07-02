package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfRetryTemplService;
import com.ald.fanbei.api.dal.dao.AfRetryTemplDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRetryTemplDo;

import java.util.List;


/**
 * 重试模板ServiceImpl
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2018-02-26 14:49:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRetryTemplService")
public class AfRetryTemplServiceImpl extends ParentServiceImpl<AfRetryTemplDo, Long> implements AfRetryTemplService {
	@Resource
	AfRetryTemplDao afRetryTemplDao;
    private static final Logger logger = LoggerFactory.getLogger(AfRetryTemplServiceImpl.class);

	@Override
	public BaseDao<AfRetryTemplDo, Long> getDao() {
		return afRetryTemplDao;
	}

	@Override
	public int deleteByBusidAndEventType(String borrowNo, String eventType) {
		return afRetryTemplDao.deleteByBusidAndEventType(borrowNo,eventType);
	}

	@Override
	public AfRetryTemplDo getByBusIdAndEventType(String busId, String eventType) {
		return afRetryTemplDao.getByBusIdAndEventType(busId,eventType);
	}

	@Override
	public List<AfRetryTemplDo> getByBusId(String busId) {
		return afRetryTemplDao.getByBusId(busId);
	}

	@Override
	public AfRetryTemplDo getCurPushDebt(String busId, String eventType) {
		return afRetryTemplDao.getCurPushDebt(busId, eventType);
	}

   
}