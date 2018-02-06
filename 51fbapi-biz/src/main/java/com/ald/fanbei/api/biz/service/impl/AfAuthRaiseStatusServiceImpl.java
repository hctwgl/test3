package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAuthRaiseStatusDao;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.biz.service.AfAuthRaiseStatusService;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-02-06 17:58:14
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAuthRaiseStatusService")
public class AfAuthRaiseStatusServiceImpl extends ParentServiceImpl<AfAuthRaiseStatusDo, Long> implements AfAuthRaiseStatusService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAuthRaiseStatusServiceImpl.class);
   
    @Resource
    private AfAuthRaiseStatusDao afAuthRaiseStatusDao;

		@Override
	public BaseDao<AfAuthRaiseStatusDo, Long> getDao() {
		return afAuthRaiseStatusDao;
	}
}