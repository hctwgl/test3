package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedRecordMaxDao;
import com.ald.fanbei.api.dal.domain.DsedRecordMaxDo;
import com.ald.fanbei.api.biz.service.DsedRecordMaxService;



/**
 * 债权推送查询上限记录表ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-17 09:28:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedRecordMaxService")
public class DsedRecordMaxServiceImpl extends ParentServiceImpl<DsedRecordMaxDo, Long> implements DsedRecordMaxService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedRecordMaxServiceImpl.class);
   
    @Resource
    private DsedRecordMaxDao dsedRecordMaxDao;

		@Override
	public BaseDao<DsedRecordMaxDo, Long> getDao() {
		return dsedRecordMaxDao;
	}

	@Override
	public DsedRecordMaxDo getByBusIdAndEventype(String busId, String eventType) {
		return dsedRecordMaxDao.getByBusIdAndEventype(busId, eventType);
	}

	@Override
	public int deleteById(Long rid) {
		return dsedRecordMaxDao.deleteById(rid) ;
	}
}