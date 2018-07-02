package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFundSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfFundSideInfoDo;
import com.ald.fanbei.api.biz.service.AfFundSideInfoService;



/**
 * '资金方账户基本信息表ServiceImpl
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:53:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFundSideInfoService")
public class AfFundSideInfoServiceImpl extends ParentServiceImpl<AfFundSideInfoDo, Long> implements AfFundSideInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFundSideInfoServiceImpl.class);
   
    @Resource
    private AfFundSideInfoDao afFundSideInfoDao;

	@Override
	public BaseDao<AfFundSideInfoDo, Long> getDao() {
		return afFundSideInfoDao;
	}
}