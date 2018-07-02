package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAdInstalmentsDao;
import com.ald.fanbei.api.dal.domain.AfAdInstalmentsDo;
import com.ald.fanbei.api.biz.service.AfAdInstalmentsService;



/**
 * 分期商品管理ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-09-21 11:07:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAdInstalmentsService")
public class AfAdInstalmentsServiceImpl implements AfAdInstalmentsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAdInstalmentsServiceImpl.class);
   
    @Resource
    private AfAdInstalmentsDao afAdInstalmentsDao;

	@Override
	public AfAdInstalmentsDo getAdInfo() {
		return afAdInstalmentsDao.getAdInfo();
	}
}