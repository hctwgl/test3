package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedNotifyRecordDao;
import com.ald.fanbei.api.dal.domain.DsedNotifyRecordDo;
import com.ald.fanbei.api.biz.service.DsedNotifyRecordService;



/**
 * 提前结清通知钱包记录表ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-17 14:03:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedNotifyRecordService")
public class DsedNotifyRecordServiceImpl extends ParentServiceImpl<DsedNotifyRecordDo, Long> implements DsedNotifyRecordService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedNotifyRecordServiceImpl.class);
   
    @Resource
    private DsedNotifyRecordDao dsedNotifyRecordDao;

		@Override
	public BaseDao<DsedNotifyRecordDo, Long> getDao() {
		return dsedNotifyRecordDao;
	}
}