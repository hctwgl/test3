package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedRetryTemplDao;
import com.ald.fanbei.api.dal.domain.DsedRetryTemplDo;
import com.ald.fanbei.api.biz.service.DsedRetryTemplService;



/**
 * 重试模板ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:28:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedRetryTemplService")
public class DsedRetryTemplServiceImpl extends ParentServiceImpl<DsedRetryTemplDo, Long> implements DsedRetryTemplService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedRetryTemplServiceImpl.class);
   
    @Resource
    private DsedRetryTemplDao dsedRetryTemplDao;

		@Override
	public BaseDao<DsedRetryTemplDo, Long> getDao() {
		return dsedRetryTemplDao;
	}

	@Override
	public DsedRetryTemplDo getByBusIdAndEventType(String busId,String eventType) {
		return dsedRetryTemplDao.getByBusIdAndEventType(busId,eventType);
	}

	@Override
	public int deleteByBusidAndEventType(String busId, String eventType) {
		return dsedRetryTemplDao.deleteByBusidAndEventType(busId,eventType);
	}
}