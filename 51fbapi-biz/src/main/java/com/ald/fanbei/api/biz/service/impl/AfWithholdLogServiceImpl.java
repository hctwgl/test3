package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfWithholdLogDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfWithholdLogDao;
import com.ald.fanbei.api.biz.service.AfWithholdLogService;



/**
 * 银行卡代扣日志ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-06 17:04:09
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afWithholdLogService")
public class AfWithholdLogServiceImpl extends ParentServiceImpl<AfWithholdLogDo, Long> implements AfWithholdLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfWithholdLogServiceImpl.class);
   
    @Resource
    private AfWithholdLogDao afWithholdLogDao;

		@Override
	public BaseDao<AfWithholdLogDo, Long> getDao() {
		return afWithholdLogDao;
	}
}