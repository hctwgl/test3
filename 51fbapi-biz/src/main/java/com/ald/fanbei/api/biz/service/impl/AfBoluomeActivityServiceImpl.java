package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:35:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityService")
public class AfBoluomeActivityServiceImpl extends ParentServiceImpl<AfBoluomeActivityDo, Long> implements AfBoluomeActivityService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityDao afBoluomeActivityDao;

		@Override
	public BaseDao<AfBoluomeActivityDo, Long> getDao() {
		return afBoluomeActivityDao;
	}
}