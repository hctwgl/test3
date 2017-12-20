package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeDianyingDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeDianyingDo;
import com.ald.fanbei.api.biz.service.AfBoluomeDianyingService;



/**
 * 新人专享ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-12-13 10:51:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeDianyingService")
public class AfBoluomeDianyingServiceImpl extends ParentServiceImpl<AfBoluomeDianyingDo, Long> implements AfBoluomeDianyingService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeDianyingServiceImpl.class);
   
    @Resource
    private AfBoluomeDianyingDao afBoluomeDianyingDao;

		@Override
	public BaseDao<AfBoluomeDianyingDo, Long> getDao() {
		return afBoluomeDianyingDao;
	}
}