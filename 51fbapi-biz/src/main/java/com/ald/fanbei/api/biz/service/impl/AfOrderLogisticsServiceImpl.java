package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfOrderLogisticsDao;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;
import com.ald.fanbei.api.biz.service.AfOrderLogisticsService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-08-21 09:28:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afOrderLogisticsService")
public class AfOrderLogisticsServiceImpl extends ParentServiceImpl<AfOrderLogisticsDo, Long> implements AfOrderLogisticsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfOrderLogisticsServiceImpl.class);
   
    @Resource
    private AfOrderLogisticsDao afOrderLogisticsDao;

		@Override
	public BaseDao<AfOrderLogisticsDo, Long> getDao() {
		return afOrderLogisticsDao;
	}

    @Override
    public AfOrderLogisticsDo getByOrderId(Long orderId) {
        return afOrderLogisticsDao.getByOrderId(orderId);
    }
}