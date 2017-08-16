package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBusinessAccessRecordsDao;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;



/**
 * 业务访问记录ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-19 16:26:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBusinessAccessRecordsService")
public class AfBusinessAccessRecordsServiceImpl extends ParentServiceImpl<AfBusinessAccessRecordsDo, Long> implements AfBusinessAccessRecordsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBusinessAccessRecordsServiceImpl.class);
   
    @Resource
    private AfBusinessAccessRecordsDao afBusinessAccessRecordsDao;

	@Override
	public BaseDao<AfBusinessAccessRecordsDo, Long> getDao() {
		return afBusinessAccessRecordsDao;
	}
}