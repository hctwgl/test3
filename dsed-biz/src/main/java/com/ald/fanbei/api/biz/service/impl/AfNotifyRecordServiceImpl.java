package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfNotifyRecordDao;
import com.ald.fanbei.api.dal.domain.AfNotifyRecordDo;
import com.ald.fanbei.api.biz.service.AfNotifyRecordService;



/**
 * 提前结清通知钱包记录表ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-28 10:06:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afNotifyRecordService")
public class AfNotifyRecordServiceImpl extends ParentServiceImpl<AfNotifyRecordDo, Long> implements AfNotifyRecordService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfNotifyRecordServiceImpl.class);
   
    @Resource
    private AfNotifyRecordDao afNotifyRecordDao;

		@Override
	public BaseDao<AfNotifyRecordDo, Long> getDao() {
		return afNotifyRecordDao;
	}
}