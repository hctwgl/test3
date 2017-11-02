package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityItemsDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityItemsService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityItemsService")
public class AfBoluomeActivityItemsServiceImpl extends ParentServiceImpl<AfBoluomeActivityItemsDo, Long> implements AfBoluomeActivityItemsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityItemsServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityItemsDao afBoluomeActivityItemsDao;

		@Override
	public BaseDao<AfBoluomeActivityItemsDo, Long> getDao() {
		return afBoluomeActivityItemsDao;
	}
}