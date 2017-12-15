package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketThresholdDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketThresholdDo;
import com.ald.fanbei.api.biz.service.AfBoluomeRedpacketThresholdService;



/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeRedpacketThresholdService")
public class AfBoluomeRedpacketThresholdServiceImpl extends ParentServiceImpl<AfBoluomeRedpacketThresholdDo, Long> implements AfBoluomeRedpacketThresholdService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeRedpacketThresholdServiceImpl.class);
   
    @Resource
    private AfBoluomeRedpacketThresholdDao afBoluomeRedpacketThresholdDao;

		@Override
	public BaseDao<AfBoluomeRedpacketThresholdDo, Long> getDao() {
		return afBoluomeRedpacketThresholdDao;
	}
}