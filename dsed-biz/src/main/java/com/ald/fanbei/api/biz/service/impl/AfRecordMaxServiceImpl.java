package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfRecordMaxDao;
import com.ald.fanbei.api.dal.domain.AfRecordMaxDo;
import com.ald.fanbei.api.biz.service.AfRecordMaxService;



/**
 * 债权推送查询上限记录表ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-11 18:10:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRecordMaxService")
public class AfRecordMaxServiceImpl extends ParentServiceImpl<AfRecordMaxDo, Long> implements AfRecordMaxService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfRecordMaxServiceImpl.class);
   
    @Resource
    private AfRecordMaxDao afRecordMaxDao;

		@Override
	public BaseDao<AfRecordMaxDo, Long> getDao() {
		return afRecordMaxDao;
	}

	@Override
	public AfRecordMaxDo getByBusIdAndEventype(String orderNo,String eventype) {
		return afRecordMaxDao.getByBusIdAndEventype(orderNo,eventype);
	}

	@Override
	public int deleteById(Long rid) {
		return afRecordMaxDao.deleteById(rid);
	}
}