package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserRebateDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserRebateDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserRebateService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityUserRebateService")
public class AfBoluomeActivityUserRebateServiceImpl extends ParentServiceImpl<AfBoluomeActivityUserRebateDo, Long> implements AfBoluomeActivityUserRebateService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityUserRebateServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityUserRebateDao afBoluomeActivityUserRebateDao;

		@Override
	public BaseDao<AfBoluomeActivityUserRebateDo, Long> getDao() {
		return afBoluomeActivityUserRebateDao;
	}
}