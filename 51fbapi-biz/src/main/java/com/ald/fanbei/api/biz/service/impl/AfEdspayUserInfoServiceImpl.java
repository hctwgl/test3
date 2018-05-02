package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfEdspayUserInfoDao;
import com.ald.fanbei.api.dal.domain.AfEdspayUserInfoDo;
import com.ald.fanbei.api.biz.service.AfEdspayUserInfoService;



/**
 * 钱包出借用户查询表ServiceImpl
 * 
 * @author gsq
 * @version 1.0.0 初始化
 * @date 2018-04-18 11:50:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afEdspayUserInfoService")
public class AfEdspayUserInfoServiceImpl extends ParentServiceImpl<AfEdspayUserInfoDo, Long> implements AfEdspayUserInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfEdspayUserInfoServiceImpl.class);
   
    @Resource
    private AfEdspayUserInfoDao afEdspayUserInfoDao;

		@Override
	public BaseDao<AfEdspayUserInfoDo, Long> getDao() {
		return afEdspayUserInfoDao;
	}
}