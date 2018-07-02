package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeOneYuanRegisterDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeOneYuanRegisterDo;
import com.ald.fanbei.api.biz.service.AfBoluomeOneYuanRegisterService;



/**
 * 一元活动用户注册记录表ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-01-26 20:04:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeOneYuanRegisterService")
public class AfBoluomeOneYuanRegisterServiceImpl extends ParentServiceImpl<AfBoluomeOneYuanRegisterDo, Long> implements AfBoluomeOneYuanRegisterService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeOneYuanRegisterServiceImpl.class);
   
    @Resource
    private AfBoluomeOneYuanRegisterDao afBoluomeOneYuanRegisterDao;

		@Override
	public BaseDao<AfBoluomeOneYuanRegisterDo, Long> getDao() {
		return afBoluomeOneYuanRegisterDao;
	}
}