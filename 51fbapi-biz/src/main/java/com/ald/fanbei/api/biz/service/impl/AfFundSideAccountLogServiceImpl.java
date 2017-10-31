package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountLogDo;
import com.ald.fanbei.api.biz.service.AfFundSideAccountLogService;



/**
 * 资金方账户变动表ServiceImpl
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:54:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFundSideAccountLogService")
public class AfFundSideAccountLogServiceImpl extends ParentServiceImpl<AfFundSideAccountLogDo, Long> implements AfFundSideAccountLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFundSideAccountLogServiceImpl.class);
   
    @Resource
    private AfFundSideAccountLogDao afFundSideAccountLogDao;

	@Override
	public BaseDao<AfFundSideAccountLogDo, Long> getDao() {
		return afFundSideAccountLogDao;
	}
}