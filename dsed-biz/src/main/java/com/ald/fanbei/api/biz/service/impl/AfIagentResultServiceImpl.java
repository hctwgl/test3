package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfIagentResultDao;
import com.ald.fanbei.api.dal.domain.AfIagentResultDo;
import com.ald.fanbei.api.biz.service.AfIagentResultService;



/**
 * 智能电核表ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:57:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afIagentResultService")
public class AfIagentResultServiceImpl extends ParentServiceImpl<AfIagentResultDo, Long> implements AfIagentResultService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfIagentResultServiceImpl.class);
   
    @Resource
    private AfIagentResultDao afIagentResultDao;

	@Override
	public BaseDao<AfIagentResultDo, Long> getDao() {
		return afIagentResultDao;
	}
	@Override
	public void updateResultByWorkId(AfIagentResultDo afIagentResultDo){
			afIagentResultDao.updateResultByWorkId(afIagentResultDo);
	}
	@Override
	public AfIagentResultDo getIagentByWorkId(long workId){
		return afIagentResultDao.getIagentByWorkId(workId);
	}

	@Override
	public AfIagentResultDo getIagentByUserIdToday(long userId) {
		return afIagentResultDao.getIagentByUserIdToday(userId);
	}
}